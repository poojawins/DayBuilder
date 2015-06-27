package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Luke on 6/27/2015.
 */
public class MyDrawingView extends View {

    private float startingX;
    private float startingY;

    public MyDrawingView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        int action = event.getAction();

        float currentX = event.getX();
        float currentY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setStartingPoint(currentX, currentY);
                result = true;
                break;
            case MotionEvent.ACTION_MOVE:
                drawTo(currentX, currentY);
                result = true;
                break;
        }
        invalidate();
        return result;
    }

    private void setStartingPoint(float startingX, float startingY) {

    }

    private void drawTo(float currentX, float currentY) {

    }
}
