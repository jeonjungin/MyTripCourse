package goldmoon.MyTripCourse.SocialCoursePost;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * 사진들 보여줄 뷰페이저 어댑터
 */

public class SocialCoursePostImgViewPagerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<String> imgRefStrs;
    public SocialCoursePostImgViewPagerAdapter(Context mContext, ArrayList<String> imgRefStrs) {
        this.mContext=mContext;
        this.imgRefStrs=imgRefStrs;
    }

    @Override
    public int getCount() {
        return imgRefStrs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageView imageView= new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(imgRefStrs.get(position));
        Glide.with(mContext)
                .load(imgRef)
                .into(imageView);

        container.addView(imageView,position);
        return imageView;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
