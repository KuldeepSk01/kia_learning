package com.app.kiyalearning.util;


public class AppValidator {

    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    //    public static final String NAME_REGEX = "^[_A-Za-z0-9-\\+]";
   // public static final String NAME_REGEX = "^[A-Za-z0-9\\s]{1,}[\\.]{0,1}[A-Za-z0-9\\s]{0,}$";
    public static final String CHAR_REGEX = ".*[a-zA-Z]+.*";
    public static final String ONLY_CHAR_REGEX = "^[a-zA-Z ]*$";
    public static final String MOBILE_REGEX = "\\d{10}";
    public static final String MOBILE_REGEX_TEST = "\\d{10}";
    public static final String YEAR_REGEX = "\\d{4}";
    public static final String PINCODE_REGEX = "^([1-9])([0-9]){5}$";
    public static final String VEHICLE_REGEX = "^[A-Z]{2} [0-9]{2} [A-Z]{2} [0-9]{4}$";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
    public static final String[] IMAGE_EXTENSIONS = new String[]{"jpg", "jpeg", "png"};
    public static final String IS_DIGITS_ONLY="^[0-9]{6,12}$";
    //public static final String isValidCardNumber="^[0-9]{6,12}$";


    public static boolean isValidEmail(String email) {
         return email.trim().matches(EMAIL_REGEX);
    }

//    public static boolean isValidEmailMobile(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(EMAIL_REGEX)) {
//            return true;
//        } else if (editText.getText().toString().matches(MOBILE_REGEX_TEST))
//            return true;
//        else {
//            editText.setError("Invalid Email ID/Mobile Number");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }
//
//
//    public static boolean isValidPassword(String password) {
//        if (password.trim().equals("")) {
//            return false;
//        } else return password.trim().length() >= 6;
//    }
//
//    public static boolean isSame(EditText editText, EditText editText2, String msg) {
//        if (!editText.getText().toString().trim().equals(editText2.getText().toString().trim())) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            editText2.setError(msg);
//            editText2.addTextChangedListener(new RemoveErrorEditText(editText));
//            return false;
//        } else
//            return true;
//    }
//
//
//    /*public static boolean isValidPassword(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(PASSWORD_REGEX))
//            return true;
//        else {
//            editText.setError("invalid password");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }*/
//
//    public static boolean isOnlyDigits( String email) {
//        return email.matches(IS_DIGITS_ONLY);
//
//    }
//
//
    public static boolean isValidMobile(String mobile) {
        if (mobile.trim().equals("")) {
            return false;
        } else return mobile.matches(MOBILE_REGEX_TEST);
    }
//
//    public static boolean isValidPincode(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(PINCODE_REGEX))
//            return true;
//        else {
//            editText.setError("invalid pincode");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }
//
//    /*public static boolean isValidName(String editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(CHAR_REGEX))
//            return true;
//        else {
//            editText.setError("invalid name");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }*/
//
//
//    public static boolean isValidName(String text) {
//        if (text.trim().equals("")) {
//
//            return false;
//        }
//        return text.matches(ONLY_CHAR_REGEX);
//    }
//
//  /*  public static boolean isValidAddress(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(NAME_REGEX))
//            return true;
//        else {
//            editText.setError("invalid address");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }*/
//
//    public static boolean isOnlyChars(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(ONLY_CHAR_REGEX))
//            return true;
//        else {
//            editText.setError("invalid format");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//
//    }
//
//
//    public static boolean isValidVehicleNo(EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().matches(VEHICLE_REGEX))
//            return true;
//        else {
//            editText.setError("invalid vehicle no.");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }
//
//
//    public static boolean isValidImage(File file) {
//        for (String extensions : IMAGE_EXTENSIONS) {
//            if (file.getName().toLowerCase().endsWith(extensions))
//                return true;
//        }
//        return false;
//    }
//
//    public static boolean isValidYear(String data) {
//        return data.matches(YEAR_REGEX);
//    }
//
//
//    public static boolean isValid(Context context, EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
//
//            editText.setError(msg);
////            AlertUtil.showToastShort(context, msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//        return true;
//    }
//
//
//    public static boolean isValidDob(int year, int month, int day) {
//        Calendar c = Calendar.getInstance();
//        int curYY = c.get(Calendar.YEAR);
//        int curMM = c.get(Calendar.MONTH);
//        int curDD = c.get(Calendar.DAY_OF_MONTH);
//        if (year <= curYY) {
//            if (year == curYY) {
//                if (month <= curMM) {
//                    return day <= curDD;
//                } else return false;
//            } else {
//                return true;
//            }
//        } else
//            return false;
//
//    }
//
//    public static void setError(EditText editText, String msg) {
//        editText.setError(msg);
//        editText.addTextChangedListener(new RemoveErrorEditText(editText));
//        editText.requestFocus();
//
//    }
//
//    public static void setErrorTextview(TextView editText, String msg) {
//        editText.setError(msg);
//        editText.addTextChangedListener(new RemoveErrorTextView(editText));
//        editText.requestFocus();
//
//    }
//
//
//    public static class RemoveErrorEditText implements TextWatcher {
//
//        private EditText editText;
//
//
//        public RemoveErrorEditText(EditText edittext) {
//            this.editText = edittext;
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//            editText.setError(null);
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before,
//                                  int count) {
//            editText.setError(null);
//        }
//
//    }
//
//
//    public static class RemoveErrorTextView implements TextWatcher {
//
//        private TextView editText;
//
//
//        public RemoveErrorTextView(TextView edittext) {
//            this.editText = edittext;
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//            editText.setError(null);
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before,
//                                  int count) {
//
//        }
//
//    }
//    public static boolean isValidCardNumber(Context context,EditText editText, String msg) {
//        if (editText.getText().toString().trim().equals("")) {
////            AlertUtil.showToastLong(context, msg);
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        } else if (editText.getText().toString().trim().length()==19)
//            return true;
//        else {
////            AlertUtil.showToastLong(context, "Invalid card number.");
//            editText.addTextChangedListener(new RemoveErrorEditText(editText));
//            editText.requestFocus();
//            return false;
//        }
//    }
}
