package com.gustavofelix.rest_spring_boot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/math")
public class MathController {

    @GetMapping(value = "/sum/{numberOne}/{numberTwo}")
    public Double sum(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return convertToDouble(numberOne) + convertToDouble(numberTwo);
    }

    @GetMapping(value = "/subtraction/{numberOne}/{numberTwo}")
    public Double subtraction(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return convertToDouble(numberOne) - convertToDouble(numberTwo);
    }

    @GetMapping(value = "/multiplication/{numberOne}/{numberTwo}")
    public Double multiplication(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return convertToDouble(numberOne) * convertToDouble(numberTwo);
    }

    @GetMapping(value = "/division/{numberOne}/{numberTwo}")
    public Double division(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return convertToDouble(numberOne) / convertToDouble(numberTwo);
    }

    @GetMapping(value = "/avg/{numberOne}/{numberTwo}")
    public Double avg(
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return (convertToDouble(numberOne) + convertToDouble(numberTwo)) / 2;
    }

    @GetMapping(value = "/sqrt/{numberOne}")
    public Double sqrt(
            @PathVariable(value = "numberOne") String numberOne
    ) throws Exception {
        if (!isNumeric(numberOne)) throw new UnsupportedEncodingException("Please set a numeric value!");

        return Math.sqrt(convertToDouble(numberOne));
    }

    private Double convertToDouble(String strNumber) throws Exception{
        if (strNumber == null || strNumber.isEmpty()) throw new UnsupportedEncodingException();

        String number = strNumber.replace(",", ".");
        return Double.parseDouble(number);
    }

    private boolean isNumeric(String strNumber) {
        if (strNumber == null || strNumber.isEmpty()) return false;

        String number = strNumber.replace(",", ".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }
}
