package com.example.currencyconversionservice.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

	@Autowired
	private CurrencyExchangeServiceProxy proxy;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/{quantity}")
	public CurrencyConversionBean getCurrecyValue(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);

		logger.info("Calling exchange service");
		ResponseEntity<CurrencyConversionBean> response = new RestTemplate().getForEntity(
				"http://localhost:8001/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
				uriVariables);

		CurrencyConversionBean convertedCurrencyBean = response.getBody();

		logger.info("{}", convertedCurrencyBean.toString());

		return new CurrencyConversionBean(convertedCurrencyBean.getId(), from, to,
				convertedCurrencyBean.getConversionMultiple(), quantity,
				quantity.multiply(convertedCurrencyBean.getConversionMultiple()), convertedCurrencyBean.getPort());
	}

	@GetMapping("/currency-conversion/feign/from/{from}/to/{to}/{quantity}")
	public CurrencyConversionBean getCurrecyValuewithFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		logger.info("Calling exchange service");
		CurrencyConversionBean convertedCurrencyBean = proxy.getExchangeValue(from, to);

		logger.info("{}", convertedCurrencyBean.toString());

		return new CurrencyConversionBean(convertedCurrencyBean.getId(), from, to,
				convertedCurrencyBean.getConversionMultiple(), quantity,
				quantity.multiply(convertedCurrencyBean.getConversionMultiple()), 
				convertedCurrencyBean.getPort());
	}

}
