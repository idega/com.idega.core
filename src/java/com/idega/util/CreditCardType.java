package com.idega.util;

import java.util.EnumSet;

public enum CreditCardType {
	INVALID {
		@Override
		public String getName() {
			return "Invalid";
		}
		
		@Override
		public String getCode() {
			return "INVALID";
		}
	},
	VISA {
		@Override
		public String getName() {
			return "Visa";
		}
		
		@Override
		public String getCode() {
			return "VISA";
		}
	},
	MASTERCARD {
		@Override
		public String getName() {
			return "Mastercard";
		}
		
		@Override
		public String getCode() {
			return "MAST";
		}
	},
	AMERICAN_EXPRESS {
		@Override
		public String getName() {
			return "American Express";
		}
		
		@Override
		public String getCode() {
			return "AMEX";
		}
	},
	EN_ROUTE {
		@Override
		public String getName() {
			return "enRoute";
		}
		
		@Override
		public String getCode() {
			return "ENRO";
		}
	},
	DINERS_CLUB {
		@Override
		public String getName() {
			return "Diner's Club";
		}
		
		@Override
		public String getCode() {
			return "DINC";
		}
	};
	
	public abstract String getName();
	public abstract String getCode();
	
	public static CreditCardType getByCode(String code) {
		CreditCardType returnValue = null;
		
        for (final CreditCardType element : EnumSet.allOf(CreditCardType.class)) {
            if (element.getCode().equals(code)) {
                returnValue = element;
            }
        }
        return returnValue;
	}
}