import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class HeaderDecoration(
    recyclerView: RecyclerView,
    private val mListener: StickyHeaderInterface
) : ItemDecoration() {
    private var mStickyHeaderHeight = 0
    init {
        recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(
                recyclerView: RecyclerView,
                motionEvent: MotionEvent
            ): Boolean {
                return if (motionEvent.y <= mStickyHeaderHeight) {
                    // Handle the clicks on the header here ...
                    true
                } else false
            }
            override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
            val topChild: View = parent.getChildAt(0)
            if (isNull(topChild)) {
                return
            }
            val topChildPosition = parent.getChildAdapterPosition(topChild)
            if (topChildPosition == RecyclerView.NO_POSITION) {
                return
            }
            val currentHeader: View = getHeaderViewForItem(topChildPosition, parent)
            fixLayoutSize(parent, currentHeader)
            val contactPoint: Int = currentHeader.getBottom()
            val childInContact: View? = getChildInContact(parent, contactPoint)
            if (isNull(childInContact)) {
                return
            }
            if (mListener.isHeader(parent.getChildAdapterPosition(childInContact!!))) {
                moveHeader(c, currentHeader, childInContact)
                return
            }
            drawHeader(c, currentHeader)
    }
    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = mListener.getHeaderPositionForItem(itemPosition)
        val layoutResId = mListener.getHeaderLayout(headerPosition)
        val header: View = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        mListener.bindHeaderData(header, headerPosition)
        return header
    }
    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }
    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View?) {
        c.save()
        c.translate(0f, nextHeader!!.getTop().toFloat() - currentHeader.getHeight().toFloat())
        currentHeader.draw(c)
        c.restore()
    }
    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            if (child.getBottom() > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        // Specs for parent (RecyclerView)
        val widthSpec: Int =
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec: Int =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.getLayoutParams().width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.getLayoutParams().height
        )
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight().also {
            mStickyHeaderHeight = it
        })
    }
    private fun isNull(view: View?): Boolean {
        return view == null
    }
    interface StickyHeaderInterface {
        /**
         * This method gets called by [HeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int
        /**
         * This method gets called by [HeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        /**
         * This method gets called by [HeaderItemDecoration] to setup the header View.
         * @param header View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View?, headerPosition: Int)

        /**
         * This method gets called by [HeaderItemDecoration] to verify whether the item represents a header.
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }
}