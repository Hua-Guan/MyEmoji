package pri.guanhua.myemoji.model.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import pri.guanhua.myemoji.BR;

public class UserBean extends BaseObservable{

    private String account;
    private String password;
    private String confirmPassword;

    @Bindable
    public String getAccount() {
        return account;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
        notifyPropertyChanged(BR.account);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        notifyPropertyChanged(BR.confirmPassword);
    }
}
