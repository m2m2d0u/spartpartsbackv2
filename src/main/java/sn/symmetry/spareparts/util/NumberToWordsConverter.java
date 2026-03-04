package sn.symmetry.spareparts.util;

import java.math.BigDecimal;

public class NumberToWordsConverter {

    private static final String[] UNITS = {
            "", "UN", "DEUX", "TROIS", "QUATRE", "CINQ", "SIX", "SEPT", "HUIT", "NEUF",
            "DIX", "ONZE", "DOUZE", "TREIZE", "QUATORZE", "QUINZE", "SEIZE",
            "DIX-SEPT", "DIX-HUIT", "DIX-NEUF"
    };

    private static final String[] TENS = {
            "", "DIX", "VINGT", "TRENTE", "QUARANTE", "CINQUANTE",
            "SOIXANTE", "SOIXANTE-DIX", "QUATRE-VINGT", "QUATRE-VINGT-DIX"
    };

    /**
     * Convert a BigDecimal amount to French words in uppercase
     * Example: 1234.56 -> "MILLE DEUX CENT TRENTE-QUATRE FRANCS CFA ET CINQUANTE-SIX CENTIMES"
     */
    public static String convertToWords(BigDecimal amount, String currency, int decimals) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "ZÉRO " + currency;
        }

        long integerPart = amount.longValue();
        long decimalPart = 0;

        if (decimals > 0) {
            BigDecimal fractionalPart = amount.subtract(new BigDecimal(integerPart));
            decimalPart = fractionalPart.multiply(BigDecimal.valueOf(Math.pow(10, decimals))).longValue();
        }

        StringBuilder result = new StringBuilder();

        // Convert integer part
        if (integerPart == 0) {
            result.append("ZÉRO");
        } else {
            result.append(convertIntegerToWords(integerPart));
        }

        // Add currency
        result.append(" ").append(currency);

        // Convert decimal part if exists
        if (decimals > 0 && decimalPart > 0) {
            result.append(" ET ").append(convertIntegerToWords(decimalPart));
            result.append(decimals == 2 ? " CENTIMES" : " DÉCIMALES");
        }

        return result.toString().trim();
    }

    private static String convertIntegerToWords(long number) {
        if (number == 0) {
            return "";
        }

        if (number < 0) {
            return "MOINS " + convertIntegerToWords(-number);
        }

        String words = "";

        // Billions
        if (number >= 1000000000) {
            long billions = number / 1000000000;
            if (billions == 1) {
                words += "UN MILLIARD ";
            } else {
                words += convertIntegerToWords(billions) + " MILLIARDS ";
            }
            number %= 1000000000;
        }

        // Millions
        if (number >= 1000000) {
            long millions = number / 1000000;
            if (millions == 1) {
                words += "UN MILLION ";
            } else {
                words += convertIntegerToWords(millions) + " MILLIONS ";
            }
            number %= 1000000;
        }

        // Thousands
        if (number >= 1000) {
            long thousands = number / 1000;
            if (thousands == 1) {
                words += "MILLE ";
            } else {
                words += convertIntegerToWords(thousands) + " MILLE ";
            }
            number %= 1000;
        }

        // Hundreds
        if (number >= 100) {
            long hundreds = number / 100;
            if (hundreds == 1) {
                words += "CENT ";
            } else {
                words += UNITS[(int) hundreds] + " CENT ";
            }
            number %= 100;
            // Add 'S' for plural hundreds if nothing follows
            if (number == 0 && hundreds > 1) {
                words = words.trim() + "S ";
            }
        }

        // Tens and units
        if (number > 0) {
            if (number < 20) {
                words += UNITS[(int) number] + " ";
            } else {
                int tens = (int) (number / 10);
                int units = (int) (number % 10);

                if (tens == 7 || tens == 9) {
                    // Special cases: 70-79 (soixante-dix) and 90-99 (quatre-vingt-dix)
                    if (tens == 7) {
                        words += "SOIXANTE-";
                        if (units == 0) {
                            words += "DIX ";
                        } else if (units == 1) {
                            words += "ET-ONZE ";
                        } else {
                            words += UNITS[10 + units] + " ";
                        }
                    } else { // tens == 9
                        words += "QUATRE-VINGT-";
                        if (units == 0) {
                            words += "DIX ";
                        } else {
                            words += UNITS[10 + units] + " ";
                        }
                    }
                } else if (tens == 8) {
                    // Special case: 80-89 (quatre-vingt)
                    words += "QUATRE-VINGT";
                    if (units == 0) {
                        words += "S ";
                    } else {
                        words += "-" + UNITS[units] + " ";
                    }
                } else {
                    words += TENS[tens];
                    if (units > 0) {
                        if (units == 1 && (tens == 2 || tens == 3 || tens == 4 || tens == 5 || tens == 6)) {
                            words += " ET " + UNITS[units] + " ";
                        } else {
                            words += "-" + UNITS[units] + " ";
                        }
                    } else {
                        words += " ";
                    }
                }
            }
        }

        return words.trim();
    }

    /**
     * Convert amount to words with default CFA currency
     */
    public static String convertToWordsCFA(BigDecimal amount, int decimals) {
        return convertToWords(amount, "FRANCS CFA", decimals);
    }

    /**
     * Convert amount to words with custom currency symbol
     */
    public static String convertToWordsWithSymbol(BigDecimal amount, String currencySymbol, int decimals) {
        // Map common currency symbols to full names
        String currencyName = switch (currencySymbol) {
            case "F", "Fr", "FCFA", "XOF" -> "FRANCS CFA";
            case "€", "EUR" -> "EUROS";
            case "$", "USD" -> "DOLLARS";
            case "£", "GBP" -> "LIVRES STERLING";
            case "CHF" -> "FRANCS SUISSES";
            default -> currencySymbol;
        };

        return convertToWords(amount, currencyName, decimals);
    }
}
