package com.cmu.privacyplugin;

public class AccDataRegex {
    private String Id = "(?i).*(?:account|user|customer|doctor" +
            "|patient|policyholder|insurer|claimant)[^\\s/(;)#|,=!>]{0,3}(id|number|no|num)";
    private  String smLoginMod = "(?i)(?:facebook|twitter|instagram|linkedin|pinterest|behance|" +
            "dribble)[^\\\\s/(;)#|,=!>]{0,2}(?:id|account|username|handle)";

    private String languPref = "(?i).*language.*";

    private  String password = "(?i)(.*(?<!(db|database|jira|sql|postgres|mongo|aws)" +
            "[^\\s/(;)#|,=!>]{0,3})(pwd|psw|pswd|password|passwrd))";

    private  String Mnemonic =  "(?i).*(mnemonic)";

    private  String accName = "(?i)(.*(?<!(db|database|jira|sql|postgres|mongo|aws)[^\\s/(;)#|,=!>]" +
            "{0,3})user[^\\s/(;)#|,=!>]{0,3}name)|(.*(account|customer|doctor|patient|" +
            "teacher|student|person|organi[zs]ation|company)[^\\s/(;)#|,=!>]{0,3}name)";

    public String getID(){
        return Id;
    }

    public String getSmLoginMod(){
        return smLoginMod;
    }
    public String getLanguPref(){
        return languPref;
    }

    public String getPassword(){
        return password;
    }

    public String getMnemonic(){
        return Mnemonic;
    }

    public String getAccName(){
        return accName;
    }

    public String getType(String code){
        if(code.matches(getID())){
            return "Account ID";
        } else if (code.matches(getSmLoginMod())) {
            return "Social Media Login Identifier";
        }else if (code.matches(getLanguPref())) {
            return "Language Preferences";
        }else if (code.matches(getPassword())) {
            return "Account Password";
        }else if (code.matches(getMnemonic())) {
            return "Mnemonic";
        }else if (code.matches(getAccName())) {
            return "Account Name";
        }else{
            return "None";
        }
    }
}
