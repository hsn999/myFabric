package main

import (
	"encoding/json"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//积分兑换记录
type ExchangeRecord struct {
	LocalUId   string `json:"localUId"`   //本地用户id（？）
	LocalUName string `json:"localUName"` //本地用户名
	LocalPoint string `json:"localPoint"` //本地点数
	Point      string `json:"point"`      //获得的积分系统点数
	Desc       string `json:"desc"`       //描述
	Time       string `json:"time"`       //兑换时间
	AppId      string `json:"appId"`      //(并不需要显示，但要能区分不同平台兑换结果)
	Tel        string `json:"tel"`        //用户电话（唯一标示）
}

func pointExchange(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	tel := dat["tel"]
	point := dat["point"]
	amount, _ := strconv.Atoi(point.(string))
	keyA := "AppId:" + appId.(string)
	keyB := "UserId:" + tel.(string)
	AesixtAsBytes, err := stub.GetState(keyA)
	if string(AesixtAsBytes) == "" || err != nil {
		message := "Failed to exchange point, System error."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(AesixtAsBytes, &appAccount)

	balanceA := appAccount.Balance
	if balanceA < amount {
		message := "Failed to exchange point, AppId account point is not enough."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	BesixtAsBytes, err := stub.GetState(keyB)
	if string(BesixtAsBytes) == "" || err != nil {
		message := "Failed to exchange point, This UserId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var userAccount UserAccount
	json.Unmarshal(BesixtAsBytes, &userAccount)
	balanceB := userAccount.Balance

	balanceA = balanceA - amount
	balanceB = balanceB + amount

	appAccount.Balance = balanceA
	appAccountAsBytes, _ := json.Marshal(appAccount)
	err = stub.PutState(keyA, appAccountAsBytes)
	if err != nil {
		message := "Failed to exchange point, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	userAccount.Balance = balanceB
	userAccountAsBytes, _ := json.Marshal(userAccount)
	err = stub.PutState(keyB, userAccountAsBytes)
	if err != nil {
		message := "Failed to exchange point, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	rev := newExchangeRecord(stub, str)
	return rev
}

func newExchangeRecord(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)

	localUId := dat["localUId"]
	localUName := dat["localUName"]
	localPoint := dat["localPoint"]
	point := dat["point"]
	desc := dat["desc"]
	appId := dat["appId"]
	tel := dat["tel"]
	ctime := time.Now().Format("2006-01-02 15:04:05")

	key := "ExchangeRecord:" + appId.(string) + ctime
	exchangeRecord := ExchangeRecord{
		LocalUId:   localUId.(string),
		LocalUName: localUName.(string),
		LocalPoint: localPoint.(string),
		Point:      point.(string),
		Desc:       desc.(string),
		Time:       ctime,
		AppId:      "ExchangeRecord:" + appId.(string),
		Tel:        "ExchangeRecord:" + tel.(string)}
	exchangeRecordAsBytes, _ := json.Marshal(exchangeRecord)
	err := stub.PutState(key, exchangeRecordAsBytes)
	if err != nil {
		message := "Failed to newExchangeRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to pointExchange."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getExchange(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getExchangeRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: false, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
