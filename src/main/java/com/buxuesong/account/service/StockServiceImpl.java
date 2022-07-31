package com.buxuesong.account.service;

import com.buxuesong.account.model.SaveStockRequest;
import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.persist.dao.StockMapper;
import com.buxuesong.account.util.HttpClientPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockMapper stockMapper;

    @Override
    public List<StockBean> getStockDetails(List<String> codes) {
        List<StockBean> stocks = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        HashMap<String, String[]> codeMap = new HashMap<>();
        for (String str : codes) {
            // 兼容原有设置
            String[] strArray;
            if (str.contains(",")) {
                strArray = str.split(",");
            } else {
                strArray = new String[] { str };
            }
            codeList.add(strArray[0]);
            codeMap.put(strArray[0], strArray);
        }

        String urlPara = String.join(",", codeList);

        try {
            String result = HttpClientPoolUtil.getHttpClient().get("http://qt.gtimg.cn/q=" + urlPara);
            String[] lines = result.split("\n");
            for (String line : lines) {
                String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
                String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
                String[] values = dataStr.split("~");
                StockBean bean = new StockBean(code, codeMap);
                bean.setName(values[1]);
                bean.setNow(values[3]);
                bean.setChange(values[31]);
                bean.setChangePercent(values[32]);
                bean.setTime(values[30]);
                bean.setMax(values[33]);// 33
                bean.setMin(values[34]);// 34

                BigDecimal now = new BigDecimal(values[3]);
                String costPriceStr = bean.getCostPrise();
                if (StringUtils.isNotEmpty(costPriceStr)) {
                    BigDecimal costPriceDec = new BigDecimal(costPriceStr);
                    BigDecimal incomeDiff = now.add(costPriceDec.negate());
                    if (costPriceDec.compareTo(BigDecimal.ZERO) <= 0) {
                        bean.setIncomePercent("0");
                    } else {
                        BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 5, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.TEN)
                            .multiply(BigDecimal.TEN)
                            .setScale(3, RoundingMode.HALF_UP);
                        bean.setIncomePercent(incomePercentDec.toString());
                    }

                    String bondStr = bean.getBonds();
                    if (StringUtils.isNotEmpty(bondStr)) {
                        BigDecimal bondDec = new BigDecimal(bondStr);
                        BigDecimal incomeDec = incomeDiff.multiply(bondDec)
                            .setScale(2, RoundingMode.HALF_UP);
                        bean.setIncome(incomeDec.toString());
                    }
                }
                stocks.add(bean);
            }
        } catch (Exception e) {

        }
        return stocks;
    }

    @Override
    public void saveStock(SaveStockRequest saveStockRequest) {
        SaveStockRequest saveStockRequestFromTable = stockMapper.findStockByCode(saveStockRequest.getCode());
        if(saveStockRequestFromTable!=null){
            stockMapper.updateStock(saveStockRequest);
        }else {
            stockMapper.save(saveStockRequest);
        }
    }

    @Override
    public void deleteStock(SaveStockRequest saveStockRequest) {
        stockMapper.deleteStock(saveStockRequest);
    }

    @Override
    public List<String> getStockList() {
        List<SaveStockRequest> stock = stockMapper.findAllStock();
        logger.info("缓存的股票为：{}", stock);
        if (stock == null || stock.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (SaveStockRequest stockRequest: stock) {
            String stockArr = stockRequest.getCode() + "," + stockRequest.getCostPrise() +"," + stockRequest.getBonds() +"," + stockRequest.getApp();
            list.add(stockArr);
        }
        return list;
    }
}
