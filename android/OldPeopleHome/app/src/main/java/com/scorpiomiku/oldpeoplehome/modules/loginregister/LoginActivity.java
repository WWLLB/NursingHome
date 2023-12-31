package com.scorpiomiku.oldpeoplehome.modules.loginregister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scorpiomiku.oldpeoplehome.R;
import com.scorpiomiku.oldpeoplehome.base.BaseActivity;
import com.scorpiomiku.oldpeoplehome.bean.Child;
import com.scorpiomiku.oldpeoplehome.bean.OldPeople;
import com.scorpiomiku.oldpeoplehome.modules.children.activity.ChildMainActivity;
import com.scorpiomiku.oldpeoplehome.modules.oldpeople.activity.OldPeopleMainActivity;
import com.scorpiomiku.oldpeoplehome.utils.LogUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.login_edt_username)
    EditText loginEdtUsername;
    @BindView(R.id.login_edt_password)
    EditText loginEdtPassword;
    @BindView(R.id.login_btn_login)
    Button loginBtnLogin;
    @BindView(R.id.linear_layout_btn_register)
    LinearLayout linearLayoutBtnRegister;
    @BindView(R.id.login_find_pwd)
    TextView loginFindPwd;
    @BindView(R.id.is_older)
    RadioButton isOlder;

    private Boolean isOlderLogin = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected Handler initHandle() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Intent intent = new Intent(LoginActivity.this, ChildMainActivity.class);
                        intent.putExtra("user", getChildUser());
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        LogUtils.shortToast("密码错误");
                        break;
                    case 3:
                        Intent intent1 = new Intent(LoginActivity.this, OldPeopleMainActivity.class);
                        intent1.putExtra("user", getOldPeopleUser());
                        startActivity(intent1);
                        break;
                }
            }
        };
    }

    @Override
    public void iniview() {
        checkPermission();
        isOlder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOlderLogin = b;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login_btn_login, R.id.linear_layout_btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn_login:
                login();
                break;
            case R.id.linear_layout_btn_register:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        String account = loginEdtUsername.getText().toString();
        String password = loginEdtPassword.getText().toString();
        data.clear();
        data.put("account", account);
        data.put("password", password);
        if (!isOlderLogin) {
            getWebUtils().loginChild(data, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.loge(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JsonObject jsonObject = getWebUtils().getJsonObj(response);
                        Gson gson = new Gson();
                        Child child = gson.fromJson(jsonObject, Child.class);
                        setUser(child);
                        handler.sendEmptyMessage(1);
                    } catch (Exception e) {
                        LogUtils.loge(e.getMessage());
                        handler.sendEmptyMessage(2);
                    }

                }
            });
        } else {
            getWebUtils().loginParent(data, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.loge(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JsonObject jsonObject = getWebUtils().getJsonObj(response);
                        Gson gson = new Gson();
                        OldPeople oldPeople = gson.fromJson(jsonObject, OldPeople.class);
                        setUser(oldPeople);
                        handler.sendEmptyMessage(3);
                    } catch (Exception e) {
                        LogUtils.loge(e.getMessage());
                        handler.sendEmptyMessage(2);
                    }

                }
            });
        }
    }

}
