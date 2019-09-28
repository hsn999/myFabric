package main

import (
	"encoding/json"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//合同信息记录
type ContractRecord struct {
	Creator        string `json:"creator"`        //创建人
	ContractName   string `json:"contractName"`   //合同名称
	ContractNumber string `json:"contractNumber"` //合同编号
	ContractorA    string `json:"contractorA"`    //签约人A
	ContractorB    string `json:"contractorB"`    //签约人B
	Desc           string `json:"desc"`           //合同描述
	Attachment     string `json:"attachment"`     //附件id（ipfs hash）
	Time           string `json:"time"`           //签约时间
	Remark         string `json:"remark"`         //备注
	Ctime          string `json:"ctime"`          //创建时间
}

func newContractRecord(str string) ContractRecord {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)

	creator := dat["creator"]
	contractName := dat["contractName"]
	contractNumber := dat["contractNumber"]
	contractorA := dat["contractorA"]
	contractorB := dat["contractorB"]
	desc := dat["desc"]
	attachment := dat["attachment"]
	qtime := dat["time"]
	remark := dat["remark"]
	ctime := time.Now().Format("2006-01-02 15:04:05")

	contractRecord := ContractRecord{
		Creator:        creator.(string),
		ContractName:   contractName.(string),
		ContractNumber: contractNumber.(string),
		ContractorA:    contractorA.(string),
		ContractorB:    contractorB.(string),
		Desc:           desc.(string),
		Attachment:     attachment.(string),
		Time:           qtime.(string),
		Remark:         remark.(string),
		Ctime:          ctime}

	return contractRecord
}

func createContract(stub shim.ChaincodeStubInterface, str string) []byte {
	contractRecord := newContractRecord(str)
	contractName := contractRecord.ContractName
	qtime := contractRecord.Time
	key := "ContractRecord:" + contractName + "-" + qtime
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) != "" || err != nil {
		message := "Failed to create ContractRecord, Duplicate ContractRecord."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	contractRecordAsBytes, _ := json.Marshal(contractRecord)
	err = stub.PutState(key, contractRecordAsBytes)
	if err != nil {
		message := "Failed to create ContractRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	txId := stub.GetTxID()
	msg := &Msg{Status: true, Code: 0, Result: txId}
	rev, _ := json.Marshal(msg)
	return rev
}

func getContractRecord(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getContractRecord, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: true, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
