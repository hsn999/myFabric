

//一定周期内用户数量
var getUserCount = function() {
	//pageDone = false;
	$.ajax({
		type:"get",
		url:config.bumeng_public + "user/v1/count",
		dataType:"json",
		success:function(returnData) {
			if(returnData.err_code == "0") {
				//pageDone = true;
				var total_count = returnData.data.totalCount;
				var set_count = $(".userNum-total").numberAnimate({num:total_count, speed:2000});
				setInterval(function() {
					$.ajax({
						type:"get",
						url:config.bumeng_public + "user/v1/count",
						dataType:"json",
						success:function(returnData) {
							total_count = returnData.data.totalCount;
							set_count.resetData(total_count);
						}
					});
				},2000);
			}
		}
	});
}

//一定周期内交易数量
var getTradeCount = function() {
	//pageDone = false;
	$.ajax({
		type:"get",
		url:config.bumeng_public + "transaction/v1/txFrequencyCount",
		dataType:"json",
		success:function(returnData) {
			if(returnData.err_code == "0") {
				//pageDone = true;
				var hour_count = returnData.data.hourCount;
				var set_count = $(".tradeNum-24h").numberAnimate({num:hour_count, speed:2000});
				setInterval(function() {
					$.ajax({
						type:"get",
						url:config.bumeng_public + "transaction/v1/txFrequencyCount",
						dataType:"json",
						success:function(returnData) {
							hour_count = returnData.data.hourCount;
							set_count.resetData(hour_count);
						}
					});
				},2000);
			}
		}
	});
}

//当前区块高度
var getLedgerSequence = function() {
	//pageDone = false;
	$.ajax({
		type:"get",
		url:config.bumeng_public + "block/v1/history",
		data:{"size":1},
		dataType: "json",
		success: function(returnData) {
			if(returnData.err_code == "0") {
				//pageDone = true;
				var ledger_seq = returnData.data[0].ledger_sequence;
				var set_ledger_seq = $(".recent-ledger").numberAnimate({num:ledger_seq, speed:2000});
				setInterval(function(){
					$.ajax({
						type:"get",
						url:config.bumeng_public + "block/v1/history",
						data:{"size":1},
						dataType: "json",
						success: function(returnData) {
							if(returnData.err_code == "0") {
								ledger_seq = returnData.data[0].ledger_sequence;
								set_ledger_seq.resetData(ledger_seq);
							}
						}
					});	
				},2000);
			}
		}
	});	
}

//当前节点数
var getNodeCount = function() {
	//pageDone = false;
	$.ajax({
		type:"get",
		url:config.bumeng_public + "node/v1/nodeList",
		dataType: "json",
		success:function(returnData){
			if(returnData.err_code == 0) {
				//pageDone = true;
				var node_count = returnData.data.length;
				var set_node_count = $(".node-total-num").numberAnimate({num:node_count, speed:2000});
				setInterval(function(){
					$.ajax({
						type:"get",
						url:config.bumeng_public + "node/v1/nodeList",
						dataType: "json",
						success: function(data) {
							node_count = data.data.length;
							set_node_count.resetData(node_count);
						}
					});
				},2000); 
			}
		}
	});	
}
//字段长度控制
var setHashStyle = function(str, len) {
	if(str) {
		var hashStyle = str.substring(0, len);
		var hash = hashStyle + "...";
		return hash;
	} else {
		console.log("err:" + str);
	}
}

var getNews = function(){
	jQuery.support.cors = true;
	$.ajax({
		type: "get",
		url:  config.bbs_bumeng + "forum.php?mod=ajax&inajax=yes&infloat=register&handlekey=register&ajaxmenu=1",
		data: {"action": "getPortals", "page": 1, "catid": 1},
		dataType: "json",
		success: function(return_data) {
			for(var i = 0; i < 3; i++) {
				var _news_url = config.bbs_bumeng + "article-" + return_data[i].aid + "-1.html";
				var _pic_src = config.bbs_bumeng + return_data[i].pic;
				var _title = return_data[i].title;
				$(".news-item").eq(i).find(".news-link").attr("href", _news_url);
				$(".news-item").eq(i).find(".news-img").attr("src", _pic_src);
				if(_title.length > 23) {
					$(".news-item").eq(i).find(".news-img-tit").text(setHashStyle(_title, 23));
				} else {
					$(".news-item").eq(i).find(".news-img-tit").text(_title);
				}
			}
		},
		error: function(data) {
			console.log(data);
		}
	});
}

$(document).ready(function() {
	getUserCount();
	getTradeCount();
	getLedgerSequence();
	getNodeCount();
	getNews();
});