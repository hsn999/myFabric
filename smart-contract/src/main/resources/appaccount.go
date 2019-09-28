package main

import (
	"encoding/json"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//应用信息
type AppInfo struct {
	AppId      string `json:"appId"`
	AppName    string `json:"appName"`    //应用名称
	AppType    string `json:"appType"`    //类型（默认0，未定义）
	Company    string `json:"company"`    //企业名称
	Contact    string `json:"contact"`    //联系人
	Tel        string `json:"tel"`        //联系人电话
	QQ         string `json:"qq"`         //联系人qq
	Desc       string `json:"desc"`       //描述
	PublicKey  string `json:"publicKey"`  //公钥
	PrivateKey string `json:"privateKey"` //私钥（后期私钥应该自己保存）
	Password   string `json:"password"`   //商家登录后台的密码
	Remark     string `json:"remark"`     //备注
	CTime      string `json:"ctime"`      //创建时间
}

//应用账号
type AppAccount struct {
	Balance int     `json:"balance"` //余额
	Status  bool    `json:"status"`  //状态（默认false正常，true冻结）
	Info    AppInfo `json:"Info"`    //应用信息
}

func newAppAccount(str string) AppAccount {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)

	appId := dat["appId"]
	appName := dat["appName"]
	appType := dat["appType"]
	company := dat["company"]
	contact := dat["contact"]
	tel := dat["tel"]
	qq := dat["qq"]
	desc := dat["desc"]
	publicKey := dat["publicKey"]
	privateKey := dat["privateKey"]
	password := dat["password"]
	remark := dat["remark"]
	ctime := time.Now().Format("2006-01-02 15:04:05")

	status := false
	balance := 0

	appInfo := AppInfo{
		AppId:      appId.(string),
		AppName:    appName.(string),
		AppType:    appType.(string),
		Company:    company.(string),
		Contact:    contact.(string),
		Tel:        tel.(string),
		QQ:         qq.(string),
		Desc:       desc.(string),
		PublicKey:  publicKey.(string),
		PrivateKey: privateKey.(string),
		Password:   password.(string),
		Remark:     remark.(string),
		CTime:      ctime}

	appAccount := AppAccount{
		Balance: balance,
		Status:  status,
		Info:    appInfo}

	return appAccount
}

func createApp(stub shim.ChaincodeStubInterface, str string) []byte {
	appAccount := newAppAccount(str)
	key := "AppId:" + appAccount.Info.AppId
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) != "" || err != nil {
		message := "Failed to create appAccount, Duplicate AppId."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	appAccountAsBytes, _ := json.Marshal(appAccount)
	err = stub.PutState(key, appAccountAsBytes)
	if err != nil {
		message := "Failed to create appAccount, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to create appAccount."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func updateApp(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	appId := dat["appId"]

	key := "AppId:" + appId.(string)
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to update AppInfo, This appId is not exist."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(esixtAsBytes, &appAccount)

	balance := appAccount.Balance
	status := appAccount.Status
	ctime := appAccount.Info.CTime

	new_appAccount := newAppAccount(str)
	new_appAccount.Balance = balance
	new_appAccount.Status = status
	new_appAccount.Info.CTime = ctime

	new_appAccountAsBytes, _ := json.Marshal(new_appAccount)
	err = stub.PutState(key, new_appAccountAsBytes)
	if err != nil {
		message := "Failed to update AppInfo, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to update AppInfo."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getAppInfo(stub shim.ChaincodeStubInterface, str string) []byte {
	key := "AppId:" + str
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to getAppById, This appId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(esixtAsBytes, &appAccount)
	appInfo := appAccount.Info
	appInfoAsBytes, _ := json.Marshal(appInfo)

	msg := &Msg{Status: true, Code: 0, Result: string(appInfoAsBytes)}
	rev, _ := json.Marshal(msg)
	return rev
}

func getPointApp(stub shim.ChaincodeStubInterface, str string) []byte {
	key := "AppId:" + str
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to getAppById, This appId is not esixt."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var appAccount AppAccount
	json.Unmarshal(esixtAsBytes, &appAccount)
	balance := appAccount.Balance
	balanceAsBytes, _ := json.Marshal(balance)

	msg := &Msg{Status: true, Code: 0, Result: string(balanceAsBytes)}
	rev, _ := json.Marshal(msg)
	return rev
}

func getAppAll(stub shim.ChaincodeStubInterface, queryString string) []byte {
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		message := "Failed to getAllApp, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	msg := &Msg{Status: true, Code: 0, Result: string(queryResults)}
	rev, _ := json.Marshal(msg)
	return rev
}
