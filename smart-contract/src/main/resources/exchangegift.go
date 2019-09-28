package main

import (
	"encoding/json"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//礼品兑换记录
type ExGiftRecord struct {
	Name      string `json:"name"`      //用户名称
	Tel       string `json:"tel"`       //用户电话（唯一标示）
	Addr      string `json:"addr"`      //用户地址（邮寄礼品用）
	Email     string `json:"email"`     //用户email
	AppId     string `json:"appId"`     //礼品提供方平台id
	GiftName  string `json:"giftName"`  //礼品名称
	GiftPoint string `json:"giftPoint"` //礼品消耗点数
	GiftId    string `json:"giftId"`    //礼品id
	Desc      string `json:"desc"`      //礼品其他属性及描述（建议json）
	Remark    string `json:"remark"`    //备注
	Time      string `json:"time"`      //兑换时间
}

func giftExchange(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	tel := dat["tel"]
	giftPoint := dat["giftPoint"]
	amount, _ := strconv.Atoi(giftPoint.(string))
	keyA := "UserId:" + tel.(string)
	keyB := "AppId:" + appId.(string)

	AesixtAsBytes, err := stub.GetState(keyA)
	if string(AesixtAsBytes) == "" || err != nil {
		message := "Failed to exchange gift, System error."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var userAccount UserAccount
	json.Unmarshal(AesixtAsBytes, &userAccount)

	balanceA := userAccount.Balance
	if balanceA < amount {
		message := "Failed to exchange gift, UserId account point is not enough."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	BesixtAsBytes, err := stub.GetState(keyB)
	if string(BesixtAsBytes) == "" || err != nil {
		message := "Failed to exchange gift, This AppId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(BesixtAsBytes, &appAccount)
	balanceB := appAccount.Balance

	balanceA = balanceA - amount
	balanceB = balanceB + amount

	userAccount.Balance = balanceA
	userAccountAsBytes, _ := json.Marshal(userAccount)
	err = stub.PutState(keyA, userAccountAsBytes)
	if err != nil {
		message := "Failed to exchange gift, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	appAccount.Balance = balanceB
	appAccountAsBytes, _ := json.Marshal(appAccount)
	err = stub.PutState(keyB, appAccountAsBytes)
	if err != nil {
		message := "Failed to exchange gift, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	rev := newExGiftRecord(stub, str)
	return rev
}

func newExGiftRecord(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	name := dat["name"]
	tel := dat["tel"]
	addr := dat["addr"]
	email := dat["email"]
	appId := dat["appId"]
	giftName := dat["giftName"]
	giftPoint := dat["giftPoint"]
	giftId := dat["giftId"]
	desc := dat["desc"]
	remark := dat["remark"]
	ctime := time.Now().Format("2006-01-02 15:04:05")

	key := "ExGiftRecord:" + tel.(string) + ctime
	exGiftRecord := ExGiftRecord{
		Name:      name.(string),
		Tel:       "ExGiftRecord:" + tel.(string),
		Addr:      addr.(string),
		Email:     email.(string),
		AppId:     "ExGiftRecord:" + appId.(string),
		GiftName:  giftName.(string),
		GiftPoint: giftPoint.(string),
		GiftId:    giftId.(string),
		Desc:      desc.(string),
		Remark:    remark.(string),
		Time:      ctime}

	exGiftRecordAsBytes, _ := json.Marshal(exGiftRecord)
	err := stub.PutState(key, exGiftRecordAsBytes)
	if err != nil {
		message := "Failed to newExGiftRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to giftExchange."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getExGift(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getExGiftRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: true, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
