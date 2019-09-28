package main

import (
	"encoding/json"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//结算记录
type DisCountRecord struct {
	AppId     string `json:"appId"`     //礼品提供方平台id
	SellPoint string `json:"sellPoint"` //结算积分点数
	Cash      string `json:"cash"`      //本次结算的现金数
	Desc      string `json:"desc"`      //结算其他属性及描述（建议json）
	Remark    string `json:"remark"`    //备注
	Time      string `json:"time"`      //结算时间
}

func disCountPoint(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	sellPoint := dat["sellPoint"]
	keyA := "AppId:" + appId.(string)
	keyB := "SystemId:zhifu"

	AesixtAsBytes, err := stub.GetState(keyA)
	if string(AesixtAsBytes) == "" || err != nil {
		message := "Failed to disCount, This AppId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(AesixtAsBytes, &appAccount)
	balanceA := appAccount.Balance

	amount, _ := strconv.Atoi(sellPoint.(string))
	if balanceA < amount {
		message := "Failed to disCount, This appId account point is not enough."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	BesixtAsBytes, err := stub.GetState(keyB)
	if string(BesixtAsBytes) == "" || err != nil {
		message := "Failed to disCount, System err."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	balanceB, _ := strconv.Atoi(string(BesixtAsBytes))

	balanceA = balanceA - amount
	balanceB = balanceB + amount

	appAccount.Balance = balanceA
	appAccountAsBytes, _ := json.Marshal(appAccount)
	err = stub.PutState(keyA, appAccountAsBytes)
	if err != nil {
		message := "Failed to disCount, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	err = stub.PutState(keyB, []byte(strconv.Itoa(balanceB)))
	if err != nil {
		message := "Failed to disCount, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	rev := newDisCountRecord(stub, str)
	return rev
}

func newDisCountRecord(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	sellPoint := dat["sellPoint"]
	cash := dat["cash"]
	desc := dat["desc"]
	remark := dat["remark"]
	ctime := time.Now().Format("2006-01-02 15:04:05")
	key := "DisCountRecord:" + appId.(string) + ctime

	disCountRecord := DisCountRecord{
		AppId:     "DisCountRecord:" + appId.(string),
		SellPoint: sellPoint.(string),
		Cash:      cash.(string),
		Desc:      desc.(string),
		Remark:    remark.(string),
		Time:      ctime}

	disCountRecordAsBytes, _ := json.Marshal(disCountRecord)
	err := stub.PutState(key, disCountRecordAsBytes)
	if err != nil {
		message := "Failed to newDisCountRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to disCountPoint."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getDisCountRecord(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getDisCountRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: true, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
