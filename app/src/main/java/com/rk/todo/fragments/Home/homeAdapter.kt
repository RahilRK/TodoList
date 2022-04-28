import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rk.todo.GlobalClass
import com.rk.todo.databinding.HomeRvItemBinding
import com.rk.todo.NotesModel

class homeAdapter(private val activity: Context,
                  private val mList: ArrayList<NotesModel>,
                  private val onClick: homeAdapterOnClick) : RecyclerView.Adapter<homeAdapter.ViewHolder>() {

    var TAG = "homeAdapter"
    lateinit var binding: HomeRvItemBinding
    var globalClass: GlobalClass = GlobalClass.getInstance(activity)

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        binding = HomeRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = mList[position]

//        globalClass.log(TAG, "onBindViewHolder: $model")

        binding.title.text = model.title
        binding.note.text = model.description

        binding.mainContainer.setOnClickListener {

            onClick.viewHomeDetail(position,model)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun restoreItem(model: NotesModel, position: Int) {

        mList.add(position, model)
        notifyItemInserted(position)
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    }

    public interface homeAdapterOnClick {

        fun viewHomeDetail(pos: Int, model: NotesModel)
    }
}