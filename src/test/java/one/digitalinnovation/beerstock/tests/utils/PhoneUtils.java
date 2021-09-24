package one.digitalinnovation.beerstock.tests.utils;

import one.digitalinnovation.beerstock.dto.PhoneDTO;
import one.digitalinnovation.beerstock.entities.Phone;
import one.digitalinnovation.beerstock.enums.BeerType;

public class PhoneUtils {

    private static final String PHONE_NUMBER = "1199999-9999";
    private static final BeerType PHONE_TYPE = BeerType.MOBILE;
    private static final long PHONE_ID = 1L;

    public static PhoneDTO createFakeDTO() {
        return PhoneDTO.builder()
                .number(PHONE_NUMBER)
                .type(PHONE_TYPE)
                .build();
    }

    public static Phone createFakeEntity() {
        return Phone.builder()
                .id(PHONE_ID)
                .number(PHONE_NUMBER)
                .type(PHONE_TYPE)
                .build();
    }
}