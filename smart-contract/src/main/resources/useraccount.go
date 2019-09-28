package main

import (
	"encoding/json"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//个人用户信息
type UserInfo struct {
	AppId    string `json:"appId"`    //提交申请的APPID,可以为系统（默认一个appid）
	Name     string `json:"name"`     //用户名称
	UserType string `json:"userType"` //类型（默认0，未定义）
	Email    string `json:"email"`    //邮箱
	Addr     string `json:"addr"`     //地址（邮寄礼品用）
	Tel      string `json:"tel"`      //电话（作为用户登录）
	QQ       string `json:"qq"`       //qq
	Desc     string `json:"desc"`     //描述
	Password string `json:"password"` //用户登录密码，创建时随机生成
	Remark   string `json:"remark"`   //备注
	CTime    string `json:"cTime"`    //创建时间
}

type UserAccount struct {
	Balance int      `json:"balance"` //余额
	Status  bool     `json:status`    //状态（默认false正常，true冻结）
	Info    UserInfo `json:"Info"`    //个人用户信息
}

func newUserAccount(str string) UserAccount {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)

	appId := dat["appId"]
	name := dat["name"]
	userType := dat["userType"]
	email := dat["email"]
	addr := dat["addr"]
	tel := dat["tel"]
	qq := dat["qq"]
	desc := dat["desc"]
	password := dat["password"]
	remark := dat["remark"]
	ctime := time.Now().Format("2006-01-02 15:04:05")

	status := false
	balance := 0

	userInfo := UserInfo{
		AppId:    appId.(string),
		Name:     name.(string),
		UserType: userType.(string),
		Email:    email.(string),
		Addr:     addr.(string),
		Tel:      tel.(string),
		QQ:       qq.(string),
		Desc:     desc.(string),
		Password: password.(string),
		Remark:   remark.(string),
		CTime:    ctime}

	userAccount := UserAccount{
		Balance: balance,
		Status:  status,
		Info:    userInfo}

	return userAccount
}

func createUser(stub shim.ChaincodeStubInterface, str string) []byte {
	userAccount := newUserAccount(str)
	key := "UserId:" + userAccount.Info.Tel
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) != "" || err != nil {
		message := "Failed to create userAccount, Duplicate UserId."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	userAccountAsBytes, _ := json.Marshal(userAccount)
	err = stub.PutState(key, userAccountAsBytes)
	if err != nil {
		message := "Failed to create userAccount, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to create userAccount."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func updateUser(stub shim.ChaincodeStubInterface, str string) []byte {
	var dat map[string]interface{}
	json.Unmarshal([]byte(str), &dat)
	tel := dat["tel"]

	key := "UserId:" + tel.(string)
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to update UserInfo, This UserId is not exist."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var userAccount UserAccount
	json.Unmarshal(esixtAsBytes, &userAccount)

	balance := userAccount.Balance
	status := userAccount.Status
	ctime := userAccount.Info.CTime

	new_userAccount := newUserAccount(str)
	new_userAccount.Balance = balance
	new_userAccount.Status = status
	new_userAccount.Info.CTime = ctime

	new_userAccountAsBytes, _ := json.Marshal(new_userAccount)
	err = stub.PutState(key, new_userAccountAsBytes)
	if err != nil {
		message := "Failed to update UserInfo, Unknown exception."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}
	message := "Succeed to update UserInfo."
	msg := &Msg{Status: true, Code: 0, Result: message}
	rev, _ := json.Marshal(msg)
	return rev
}

func getUserInfo(stub shim.ChaincodeStubInterface, str string) []byte {
	key := "UserId:" + str
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to get UserInfo, This UserId is not exist."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var userAccount UserAccount
	json.Unmarshal(esixtAsBytes, &userAccount)
	userInfo := userAccount.Info
	userInfoAsBytes, _ := json.Marshal(userInfo)

	msg := &Msg{Status: true, Code: 0, Result: string(userInfoAsBytes)}
	rev, _ := json.Marshal(msg)
	return rev
}

func getPointUser(stub shim.ChaincodeStubInterface, str string) []byte {
	key := "UserId:" + str
	esixtAsBytes, err := stub.GetState(key)
	if string(esixtAsBytes) == "" || err != nil {
		message := "Failed to get UserPoint, This UserId is not exist."
		msg := &Msg{Status: false, Code: 0, Result: message}
		rev, _ := json.Marshal(msg)
		return rev
	}

	var userAccount UserAccount
	json.Unmarshal(esixtAsBytes, &userAccount)
	balance := userAccount.Balance
	balanceAsBytes, _ := json.Marshal(balance)

	msg := &Msg{Status: true, Code: 0, Result: string(balanceAsBytes)}
	rev, _ := json.Marshal(msg)
	return rev
}
