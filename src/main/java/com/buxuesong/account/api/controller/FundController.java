package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.StockAndFundBean;
import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.FundService;
import com.buxuesong.account.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FundController {

    @Autowired
    private FundService fundService;

    @Autowired
    private StockService stockService;

    /**
     * 获取基金信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/fund")
    public Response getFundList(HttpServletRequest request) throws Exception {
        List<String> fundListFromRedis = fundService.getFundList();
        return Response.builder().code("00000000").value(fundService.getFundDetails(fundListFromRedis)).build();
    }

    /**
     * 保存/修改基金接口
     *
     * @return
     */
    @PostMapping(value = "/saveFund")
    public Response saveFund(@RequestBody SaveFundRequest saveFundRequest) throws Exception {
        log.info("Save fund request: {}", saveFundRequest);
        if (fundService.saveFund(saveFundRequest)) {
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 删除基金接口
     *
     * @return
     */
    @PostMapping(value = "/deleteFund")
    public Response deleteFund(@RequestBody SaveFundRequest saveFundRequest) throws Exception {
        log.info("Delete fund request: {}", saveFundRequest);
        fundService.deleteFund(saveFundRequest);
        return Response.builder().value(true).code("00000000").build();
    }

    /**
     * 获取基金信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/stockAndFund")
    public Response getStockAndFundList(HttpServletRequest request) throws Exception {
        List<String> fundListFrom = fundService.getFundList();
        List<String> stcokListFrom = stockService.getStockList();
        List<FundBean> funds = fundService.getFundDetails(fundListFrom);
        List<StockBean> stocks = stockService.getStockDetails(stcokListFrom);
        List<StockAndFundBean> stockAndFundsFromFunds = funds.stream()
            .map(s -> StockAndFundBean.builder().type("FUND").code(s.getFundCode())
                .name(s.getFundName()).costPrise(s.getCostPrise()).bonds(s.getBonds())
                .app(s.getApp()).incomePercent(s.getIncomePercent()).income(s.getIncome())
                // 基金部分内容
                .jzrq(s.getJzrq()).dwjz(s.getDwjz()).gsz(s.getGsz())
                .gszzl(s.getGszzl()).gztime(s.getGztime())
                .build())
            .collect(Collectors.toList());
        List<StockAndFundBean> stockAndFundsFromStocks = stocks.stream()
            .map(s -> StockAndFundBean.builder().type("STOCK").code(s.getCode()).name(s.getName())
                .costPrise(s.getCostPrise()).bonds(s.getBonds()).app(s.getApp())
                .incomePercent(s.getIncomePercent()).income(s.getIncome())
                // 股票部分内容
                .now(s.getNow()).change(s.getChange()).changePercent(s.getChangePercent())
                .time(s.getTime()).max(s.getMax()).min(s.getMin())
                .build())
            .collect(Collectors.toList());
        stockAndFundsFromStocks.addAll(stockAndFundsFromFunds);
        return Response.builder().code("00000000").value(stockAndFundsFromStocks).build();
    }
}
