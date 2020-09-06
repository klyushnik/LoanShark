//Non-scrollable listview
//Needed for phone numbers and non-monetary items

package sanguine.studio.loanshark;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class NonScrollListView extends ListView {

    //constructors
    public NonScrollListView(Context context){
        super(context);
    }

    public NonScrollListView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public NonScrollListView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    public void onMeasure(int width, int height){
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(width, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
