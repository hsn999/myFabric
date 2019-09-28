package main

import (
	"bytes"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type Msg struct {
	Status bool   `json:"Status"`
	Code   int    `json:"Code"`
	Result string `json:"Result"`
}

type SmartContract struct {
}

func (s *SmartContract) Init(stub shim.ChaincodeStubInterface) pb.Response {
	var err error
	total := 100000000000
	key := "SystemId:zhifu"
	err = stub.PutState(key, []byte(strconv.Itoa(total)))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (s *SmartContract) Query(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "getAllApp" {
		return s.getAllApp(stub, args)
	} else if function == "getAppById" {
		return s.getAppById(stub, args)
	} else if function == "getAppPoint" {
		return s.getAppPoint(stub, args)
	} else if function == "getAppRechargeRecord" {
		return s.getAppRechargeRecord(stub, args)
	} else if function == "getExchangeRecord" {
		return s.getExchangeRecord(stub, args)
	} else if function == "getExchangeRecordByDate" {
		return s.getExchangeRecordByDate(stub, args)
	} else if function == "getUserExchangeRecord" {
		return s.getUserExchangeRecord(stub, args)
	} else if function == "getUserAllExchangeRecord" {
		return s.getUserAllExchangeRecord(stub, args)
	} else if function == "getUserInfoByUserId" {
		return s.getUserInfoByUserId(stub, args)
	} else if function == "getUserPoint" {
		return s.getUserPoint(stub, args)
	} else if function == "getExGiftRecord" {
		return s.getExGiftRecord(stub, args)
	} else if function == "getExGiftRecordBydate" {
		return s.getExGiftRecordBydate(stub, args)
	} else if function == "getExGiftRecordByUserId" {
		return s.getExGiftRecordByUserId(stub, args)
	} else if function == "getAllExGiftRecordByUserId" {
		return s.getAllExGiftRecordByUserId(stub, args)
	} else if function == "getDisCountRecordByAppId" {
		return s.getDisCountRecordByAppId(stub, args)
	} else if function == "getDisCountRecordByDate" {
		return s.getDisCountRecordByDate(stub, args)
	} else if function == "getContractRecordByCreator" {
		return s.getContractRecordByCreator(stub, args)
	} else if function == "getContractRecordByNumber" {
		return s.getContractRecordByNumber(stub, args)
	}

	return shim.Error("Invalid Smart Contract function name.")
}

func (s *SmartContract) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "createAppAccount" {
		return s.createAppAccount(stub, args)
	} else if function == "updateAppInfo" {
		return s.updateAppInfo(stub, args)
	} else if function == "getAllApp" {
		return s.getAllApp(stub, args)
	} else if function == "getAppById" {
		return s.getAppById(stub, args)
	} else if function == "getAppPoint" {
		return s.getAppPoint(stub, args)
	} else if function == "rechargeByAppId" {
		return s.rechargeByAppId(stub, args)
	} else if function == "getAppRechargeRecord" {
		return s.getAppRechargeRecord(stub, args)
	} else if function == "getExchangeRecord" {
		return s.getExchangeRecord(stub, args)
	} else if function == "getExchangeRecordByDate" {
		return s.getExchangeRecordByDate(stub, args)
	} else if function == "getUserExchangeRecord" {
		return s.getUserExchangeRecord(stub, args)
	} else if function == "getUserAllExchangeRecord" {
		return s.getUserAllExchangeRecord(stub, args)
	} else if function == "createUserAccount" {
		return s.createUserAccount(stub, args)
	} else if function == "getUserInfoByUserId" {
		return s.getUserInfoByUserId(stub, args)
	} else if function == "updateUserAccount" {
		return s.updateUserAccount(stub, args)
	} else if function == "exchangePoint" {
		return s.exchangePoint(stub, args)
	} else if function == "getUserPoint" {
		return s.getUserPoint(stub, args)
	} else if function == "exchangeGift" {
		return s.exchangeGift(stub, args)
	} else if function == "getExGiftRecord" {
		return s.getExGiftRecord(stub, args)
	} else if function == "getExGiftRecordBydate" {
		return s.getExGiftRecordBydate(stub, args)
	} else if function == "getExGiftRecordByUserId" {
		return s.getExGiftRecordByUserId(stub, args)
	} else if function == "getAllExGiftRecordByUserId" {
		return s.getAllExGiftRecordByUserId(stub, args)
	} else if function == "disCount" {
		return s.disCount(stub, args)
	} else if function == "getDisCountRecordByAppId" {
		return s.getDisCountRecordByAppId(stub, args)
	} else if function == "getDisCountRecordByDate" {
		return s.getDisCountRecordByDate(stub, args)
	} else if function == "createContractRecord" {
		return s.createContractRecord(stub, args)
	} else if function == "getContractRecordByCreator" {
		return s.getContractRecordByCreator(stub, args)
	} else if function == "getContractRecordByNumber" {
		return s.getContractRecordByNumber(stub, args)
	}

	return shim.Error("Invalid Smart Contract function name.")
}

func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}

func (s *SmartContract) createAppAccount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := createApp(stub, str)
	sendEvent(stub)
	return shim.Success(result)
}

func (s *SmartContract) updateAppInfo(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := updateApp(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getAllApp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	key := "0"
	queryString := fmt.Sprintf("{\"selector\": {\"Info\":{\"appType\":{\"$gte\": \"%s\"}}}}", key)
	result := getAppAll(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getAppById(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := getAppInfo(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getAppPoint(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := getPointApp(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) rechargeByAppId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := recharge(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getAppRechargeRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	key := "RechargeRecord:" + str
	queryString := fmt.Sprintf("{\"selector\":{\"appId\":\"%s\"}}", key)
	result := getRecharge(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getExchangeRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	key := "ExchangeRecord:" + str
	queryString := fmt.Sprintf("{\"selector\":{\"appId\":\"%s\"}}", key)
	result := getExchange(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getExchangeRecordByDate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	start := args[1]
	end := args[2]
	key := "ExchangeRecord:" + appId
	queryString := fmt.Sprintf("{\"selector\": {\"$and\": [{\"appId\":\"%s\"},{\"time\": {\"$gte\": \"%s\"}},{\"time\": {\"$lte\": \"%s\"}}]}}", key, start, end)
	result := getExchange(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getUserExchangeRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	userId := args[1]
	queryString := fmt.Sprintf("{\"selector\": {\"$and\": [{\"appId\":\"%s\"},{\"tel\": \"%s\"}]}}", "ExchangeRecord:"+appId, "ExchangeRecord:"+userId)
	result := getExchange(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getUserAllExchangeRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	userId := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"tel\":\"%s\"}}", "ExchangeRecord:"+userId)
	result := getExchange(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) createUserAccount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := createUser(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getUserInfoByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := getUserInfo(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) updateUserAccount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := updateUser(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) exchangePoint(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := pointExchange(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getUserPoint(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := getPointUser(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) exchangeGift(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := giftExchange(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getExGiftRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"appId\":\"%s\"}}", "ExGiftRecord:"+appId)
	result := getExGift(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getExGiftRecordBydate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	start := args[1]
	end := args[2]
	key := "ExGiftRecord:" + appId
	queryString := fmt.Sprintf("{\"selector\": {\"$and\": [{\"appId\":\"%s\"},{\"time\": {\"$gte\": \"%s\"}},{\"time\": {\"$lte\": \"%s\"}}]}}", key, start, end)
	result := getExGift(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getExGiftRecordByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	userId := args[1]
	queryString := fmt.Sprintf("{\"selector\":{\"$and\": [{\"appId\":\"%s\"},{\"tel\":\"%s\"}]}}", "ExGiftRecord:"+appId, "ExGiftRecord:"+userId)
	result := getExGift(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getAllExGiftRecordByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	userId := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"tel\":\"%s\"}}", "ExGiftRecord:"+userId)
	result := getExGift(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) disCount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := disCountPoint(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getDisCountRecordByAppId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"appId\":\"%s\"}}", "DisCountRecord:"+appId)
	result := getDisCountRecord(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getDisCountRecordByDate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	appId := args[0]
	start := args[1]
	end := args[2]
	key := "DisCountRecord:" + appId
	queryString := fmt.Sprintf("{\"selector\":{\"$and\": [{\"appId\":\"%s\"},{\"time\": {\"$gte\": \"%s\"}},{\"time\": {\"$lte\": \"%s\"}}]}}", key, start, end)
	result := getDisCountRecord(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) createContractRecord(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	str := args[0]
	result := createContract(stub, str)
	return shim.Success(result)
}

func (s *SmartContract) getContractRecordByCreator(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	creator := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"creator\":\"%s\"}}", creator)
	result := getContractRecord(stub, queryString)
	return shim.Success(result)
}

func (s *SmartContract) getContractRecordByNumber(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	contractNumber := args[0]
	queryString := fmt.Sprintf("{\"selector\": {\"contractNumber\":\"%s\"}}", contractNumber)
	result := getContractRecord(stub, queryString)
	return shim.Success(result)
}

func main() {
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error starting SmartContract chaincode: %s", err)
	}
}
