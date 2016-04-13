package kerecs.bluetoothsignal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by LiuJun on 2016/4/4.
 */
public class DevicesAdapter extends ArrayAdapter<Device> {
    private Context context;
    private int mResourceId;
    private ArrayList<Device> listData;

    public DevicesAdapter(Context context, int textViewResourceId, ArrayList<Device> listData) {
        super(context, textViewResourceId);
        this.context = context;
        this.mResourceId = textViewResourceId;
        this.listData = listData;
    }

    public final class Holder {                //自定义控件集合
        public LinearLayout background;
        public TextView deviceName;
        public TextView deviceDistance;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            //获取list_item布局文件的视图
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(mResourceId, null);
            //获取控件对象
            holder.background = (LinearLayout) view.findViewById(R.id.background);
            holder.deviceName = (TextView) view.findViewById(R.id.deviceName);
            holder.deviceDistance = (TextView) view.findViewById(R.id.deviceDistance);
            //设置控件集到convertView
            view.setTag(holder);
        }else {//复用
            holder = (Holder)view.getTag();
        }

        Device device = listData.get(position);
        //设置文字和图片
        holder.deviceName.setText(device.getName());
        holder.deviceDistance.setText(new DecimalFormat("0.00").format(device.getDistance()) + "m");

        return view;
    }
}
