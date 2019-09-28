package main

import (
	"fmt"
	"log"
	"runtime"
	"strconv"
)

var skip int = 2

type Logger struct {
}

var logger = &Logger{}

func (this *Logger) Info(str string) {
	this.console("[INFO ]:", str)
}

func (this *Logger) Error(str string) {
	this.console("[ERROR]:", str)
}

func (this *Logger) console(v ...interface{}) {
	s := fmt.Sprint(v...)
	_, file, line, _ := runtime.Caller(skip)
	short := file
	for i := len(file) - 1; i > 0; i-- {
		if file[i] == '/' {
			short = file[i+1:]
			break
		}
	}
	file = short
	log.Println(file, "line:"+strconv.Itoa(line), s)
}
