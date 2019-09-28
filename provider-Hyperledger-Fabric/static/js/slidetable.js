function autoScroll(obj) {
    $(obj).find("ul").animate({
        marginTop: "-30px"
    }, 1500, function () {
        $(this).css({marginTop: "0px"}).find("li:first").appendTo(this);
    })
}

// $(function () {
//     var cc = setInterval('autoScroll(".maquee")', 3000);
//
//
//     //悬停时停止滑动，离开时继续执行
//     $('.list_ul').children("li").hover(function () {
//         clearInterval(cc);			//清除自动滑动动画
//     }, function () {
//         cc = setInterval('autoScroll(".maquee")', 3000);	//继续执行动画
//     })
// });

