var pageSize = 15;
var filteredApp = "ALL";

function getData() {
    var userId = $("#userId").val();
    var personName = $("#personName").val();
    var accountId = $("#accountId").val();
    $.ajax({
        url:"/fund",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = getTableHtml(result);
            $("#nr").html(str);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });

    $.ajax({
        url:"/fund/table",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            $("#fund").val(result);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    lay('#version').html('-v'+ laydate.v);

}

function getTableHtml(result){
    var str = "";
    var totalIncome = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var totalDayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var totalmarketValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = new BigDecimal(parseFloat((new BigDecimal(result[k].gszzl)).multiply((new BigDecimal(result[k].dwjz))).multiply(new BigDecimal(result[k].bonds)).divide(new BigDecimal("100"))).toFixed(2));
        marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].gsz)).multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        totalDayIncome = totalDayIncome.add(dayIncome);
        totalmarketValue = totalmarketValue.add(marketValue);
        totalIncome = totalIncome.add(new BigDecimal(result[k].income));
        str += "<tr><td>"
            + "<a onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + result[k].fundCode
            + "</td><td>" +result[k].fundName
            + "</td><td>" +result[k].gszzl + "%"
            + "</td><td>" + dayIncome
            + "</td><td>" + result[k].dwjz
            + "</td><td>" + result[k].gsz
            + "</td><td>" +result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + result[k].incomePercent + "%"
            + "</td><td>" + marketValue
            + "</td><td>" + result[k].income
            +"</td></tr>";

    }
    str += "<tr><td>合计</td><td colspan='3'></td><td>" + totalDayIncome + "</td><td colspan='5'></td><td>" + totalmarketValue + "</td><td>" + totalIncome
        +"</td></tr>";
    return str;
}

function saveFund(){
    var userId = $("#userId").val();
    var fund = $("#fund").val();
    var req = {
        "userId" : userId,
        "fund": fund
    }
    $.ajax({
        url:"/fund",
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code=="00000000"){
                getData();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function getAppName(app){
    if(app == "ZFB"){
        return "支付宝";
    } else if(app == "DFCF"){
        return "东方财富";
    } else if(app == "DFZQ"){
        return "东方证券";
    } else if(app == "ZGYH"){
        return "中国银行";
    }
}

function filterApp(app) {
    filteredApp = app;
    getData();
}
