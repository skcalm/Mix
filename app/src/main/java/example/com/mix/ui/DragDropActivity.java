package example.com.mix.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import example.com.mix.R;

public class DragDropActivity extends BaseActivity{
    private static final String TAG = "DragDrop";
    private ImageView imgIcon1;
    private ImageView imgIcon2;
    private View viewBtmArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop);

        initVal();
        initView();
    }

    private void initVal(){
        imgIcon1 = (ImageView) findViewById(R.id.icon1);
        imgIcon2 = (ImageView) findViewById(R.id.icon2);
        viewBtmArea = findViewById(R.id.bottomArea);
    }

    private void initView(){
        imgIcon1.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imgIcon1.startDragAndDrop(null, new View.DragShadowBuilder(imgIcon1), null, 0);
                }else{
                    imgIcon1.startDrag(null, new View.DragShadowBuilder(imgIcon1), null, 0);
                }
                return true;
            }
        });

        imgIcon1.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d(TAG, "imgIcon1 onDrag() action=" + event.getAction());
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                }

                return false;
            }
        });

        imgIcon2.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d(TAG, "imgIcon2 onDrag() action=" + event.getAction());
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                }

                return false;
            }
        });

        viewBtmArea.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d(TAG, "viewBtmArea onDrag() action=" + event.getAction());
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                }

                return false;
            }
        });
    }
}
