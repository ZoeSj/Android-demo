package fragment.com.hulufragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Btton_a1;
    private Button Btton_a2;
    private Button Btton_a3;
    private Button Btton_a4;
    private Button Btton_a5;
    private Button Btton_a6;
    private Button Btton_a7;
    private TextView Activity_tv;
    private EditText Activity_et;
    private Button Activity_callChild;
    private FrameLayout temp;
    private FragmentManager fragmentManager;
    private fragment1 fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        extracted();
    }

    private void extracted() {
        fragment1 = new fragment1();
        fragmentManager = this.getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.replace(R.id.temp, fragment1);
        beginTransaction.commit();
    }


    private void initview() {
        Btton_a1 = (Button) findViewById(R.id.Btton_a1);
        Btton_a2 = (Button) findViewById(R.id.Btton_a2);
        Btton_a3 = (Button) findViewById(R.id.Btton_a3);
        Btton_a4 = (Button) findViewById(R.id.Btton_a4);
        Btton_a5 = (Button) findViewById(R.id.Btton_a5);
        Btton_a6 = (Button) findViewById(R.id.Btton_a6);
        Btton_a7 = (Button) findViewById(R.id.Btton_a7);
        Activity_tv = (TextView) findViewById(R.id.Activity_tv);
        Activity_et = (EditText) findViewById(R.id.Activity_et);
        Activity_callChild = (Button) findViewById(R.id.Activity_callChild);
        temp = (FrameLayout) findViewById(R.id.temp);

        Btton_a1.setOnClickListener(this);
        Btton_a2.setOnClickListener(this);
        Btton_a3.setOnClickListener(this);
        Btton_a4.setOnClickListener(this);
        Btton_a5.setOnClickListener(this);
        Btton_a6.setOnClickListener(this);
        Btton_a7.setOnClickListener(this);
        Activity_callChild.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        fragmentManager = this.getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.Btton_a1:
                extracted();
                break;
            case R.id.Btton_a2:
                fragment2 fragement2 = new fragment2();
                beginTransaction.replace(R.id.temp, fragement2);
                break;
            case R.id.Btton_a3:
                fragment3 fragment3 = new fragment3();
                beginTransaction.replace(R.id.temp, fragment3);
                break;
            case R.id.Btton_a4:
                fragment4 fragment4 = new fragment4();
                beginTransaction.replace(R.id.temp, fragment4);
                break;
            case R.id.Btton_a5:
                fragment5 fragment5 = new fragment5();
                beginTransaction.replace(R.id.temp, fragment5);
                break;
            case R.id.Btton_a6:
                fragment6 fragment6 = new fragment6();
                beginTransaction.replace(R.id.temp, fragment6);
                break;
            case R.id.Btton_a7:
                fragment7 fragment7 = new fragment7();
                beginTransaction.replace(R.id.temp, fragment7);
                break;
            case R.id.Activity_callChild:

                break;
        }
        beginTransaction.commit();
    }

    private void submit() {
        // validate
        String et = Activity_et.getText().toString().trim();
        if (TextUtils.isEmpty(et)) {
            Toast.makeText(this, "输入不能为null", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
