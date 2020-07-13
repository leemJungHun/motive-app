package kr.rowan.motive_app.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.data.MyTeacherItem;

public class MyTeacherRecyclerAdapter extends RecyclerView.Adapter<MyTeacherRecyclerAdapter.ViewHolder> {
    private ArrayList<MyTeacherItem> mData = null ;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MyTeacherRecyclerAdapter(ArrayList<MyTeacherItem> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    @NonNull
    public MyTeacherRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        View view = LayoutInflater.from(context).inflate(R.layout.item_myteacher, parent, false) ;
        return new MyTeacherRecyclerAdapter.ViewHolder(view) ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyTeacherRecyclerAdapter.ViewHolder holder, int position) {

        MyTeacherItem item = mData.get(position) ;

        //holder.scheduleIcon.setImageDrawable(item.getIcon()) ;
        holder.teacherName.setText(item.getTeacherName()) ;
        holder.teacherImg.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            holder.teacherImg.setClipToOutline(true);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public void updateData(ArrayList<MyTeacherItem> myTeacherItems) {
        mData.clear();
        mData.addAll(myTeacherItems);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView teacherImg ;
        TextView teacherName ;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            teacherName = itemView.findViewById(R.id.teacher_name) ;
            teacherImg = itemView.findViewById(R.id.teacher_img) ;
        }
    }
}
