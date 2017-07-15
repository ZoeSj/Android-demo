package fragment.com.hulufragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/1.
 */
public class fragment2 extends Fragment {

    private TextView tv_temp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, null);
        //fragment的布局控件的查找,就要用到inflater得到的VIew对象.
       return view;
    }


}
