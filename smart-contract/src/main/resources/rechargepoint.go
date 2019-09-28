package main

import (
	"encoding/json"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//充值记录
type RechargeRecord struct {
	AppId string `json:"appId"` //应用（商家）唯一id
	Time  string `json:"time"`  //充值时间
	Cash  string `json:"cash"`  //本次充值的现金数
	Point string `json:"point"` //充值点数
	Desc  string `json:"desc"`  //描述
}

func recharge(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	point := dat["point"]

	keyA := "SystemId:zhifu"
	keyB := "AppId:" + appId.(string)

	AesixtAsBytes, err := stub.GetState(keyA)
	if string(AesixtAsBytes) == "" || err != nil {
		message := "Failed to recharge, System error."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	balanceA, _ := strconv.Atoi(string(AesixtAsBytes))
	amount, _ := strconv.Atoi(point.(string))
	if balanceA < amount {
		message := "Failed to recharge, System account point is not enough."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	BesixtAsBytes, err := stub.GetState(keyB)
	if string(BesixtAsBytes) == "" || err != nil {
		message := "Failed to recharge, This AppId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(BesixtAsBytes, &appAccount)
	balanceB := appAccount.Balance

	balanceA = balanceA - amount
	balanceB = balanceB + amount

	err = stub.PutState(keyA, []byte(strconv.Itoa(balanceA)))
	if err != nil {
		message := "Failed to recharge, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	appAccount.Balance = balanceB
	appAccountAsBytes, _ := json.Marshal(appAccount)
	err = stub.PutState(keyB, appAccountAsBytes)
	if err != nil {
		message := "Failed to recharge, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	rev := newRechargeRecord(stub, str)
	return rev
}

func newRechargeRecord(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]
	cash := dat["cash"]
	point := dat["point"]
	desc := dat["desc"]
	ctime := time.Now().Format("2006-01-02 15:04:05")
	key := "RechargeRecord:" + appId.(string) + ctime

	rechargeRecord := RechargeRecord{
		AppId: "RechargeRecord:" + appId.(string),
		Time:  ctime,
		Cash:  cash.(string),
		Point: point.(string),
		Desc:  desc.(string)}

	rechargeRecordAsBytes, _ := json.Marshal(rechargeRecord)
	err := stub.PutState(key, rechargeRecordAsBytes)
	if err != nil {
		message := "Failed to newRechargeRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to recharge."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getRecharge(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getRecharge, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: true, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
