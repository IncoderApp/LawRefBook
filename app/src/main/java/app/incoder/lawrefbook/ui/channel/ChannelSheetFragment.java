package app.incoder.lawrefbook.ui.channel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.room.Transaction;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.sqlite.Sqlite3Dao;
import app.incoder.lawrefbook.storage.Category;
import app.incoder.lawrefbook.storage.channel.Channel;
import app.incoder.lawrefbook.storage.channel.ChannelRepository;
import app.incoder.lawrefbook.storage.channel.ChannelViewModel;
import app.incoder.lawrefbook.util.ConstantManager;
import app.incoder.lawrefbook.widget.grid.Bean;
import lombok.Getter;
import lombok.Setter;

/**
 * 分类管理
 *
 * @author Jerry Xu
 * @since 2025/6/8 14:48
 */
public class ChannelSheetFragment extends BottomSheetDialogFragment {

    /*** mCategories: 数据库存储的栏目； mCategoriesSorted: 用户调整后的栏目(待存储数据库)**/
    private List<Category> mCategories, mCategoriesSorted;
    private Chip mEdit, mRest;
    private RecyclerView mRecyclerView;
    private ItemAdapter adapter;
    private ItemTouchHelper touchHelper;
    private AtomicBoolean isEdit;
    private List<Bean> show = new ArrayList<>();
    private List<Bean> sortList = new ArrayList<>();
    private final List<Channel> channelSortList = new ArrayList<>();
    private ChannelViewModel mViewModel;
    private LiveData<List<Channel>> allChannelsLive;
    private static boolean editStatus = false;
    /*** 标记分类有变更（显示-->隐藏，隐藏-->显示，排序）**/
    private static boolean changed = false;
    private static final String SHOW_CHANNEL_TITLE = "显示分类(拖拽排序)";
    private static final String HIDE_CHANNEL_TITLE = "隐藏分类";

    public interface OnBottomSheetDismissListener {
        void onBottomSheetDismissed();

        void onBottomSheetConfirmed(List<Category> categoriesSorted);
    }

    private OnBottomSheetDismissListener listener;

    public void setOnBottomSheetDismissListener(OnBottomSheetDismissListener listener) {
        this.listener = listener;
    }

    public static ChannelSheetFragment newInstance() {
        ChannelSheetFragment mBottomSheet = new ChannelSheetFragment();
        Bundle args = new Bundle();
        mBottomSheet.setArguments(args);
        mBottomSheet.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
        return mBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        allChannelsLive = mViewModel.getAllChannelsLive();
        mCategories = Sqlite3Dao.categoryList(requireContext());
        if (!show.isEmpty()) {
            show.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sheet_channel, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = requireView().findViewById(R.id.rv_channel);
        mEdit = requireView().findViewById(R.id.c_edit_save);
        mRest = requireView().findViewById(R.id.c_rest);

        isEdit = new AtomicBoolean(false);
        mEdit.setOnClickListener(v -> {
            if (mEdit.isCheckedIconVisible() && isEdit.get()) {
                // 保存
                isEdit.set(false);
                mRest.setVisibility(View.GONE);
                mEdit.setText("编辑");
                adapter.notifyDataSetChanged();
                if (changed) {
                    // 有做排序调整
                    if (!channelSortList.isEmpty()) {
                        channelSortList.clear();
                    }
                    editStatus = false;
                    mCategoriesSorted = new ArrayList<>(show.size());
                    for (int i = 0; i < show.size(); i++) {
                        Bean bean = show.get(i);
                        if (bean.getItemType() == Bean.TYPE_ITEM) {
                            Channel channel = new Channel();
                            channel.setCategoryName(bean.getTitle());
                            channel.setSortNo(i);
                            channel.setShow(bean.isShowClose());
                            // TODO,这里更新时间要变
//                            channel.setCreateTime(LocalDateTime.now());
//                            channel.setUpdateTime(LocalDateTime.now());
                            mCategories.stream().filter(x -> x.getName().equals(bean.getTitle()))
                                    .findFirst()
                                    .ifPresent(category -> mCategoriesSorted.add(category));

                            channelSortList.add(channel);
                        }
                    }
                    updateSort(channelSortList);
                } else {
                    dismiss();
                }
            } else {
                editBehavior();
            }
        });
        mRest.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(null)
                .setMessage("确定将栏目设置恢复到默认状态？")
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .setPositiveButton("确定", (dialog, which) -> {
                    mViewModel.deleteAllChannels();
                    show.clear();
                    defaultChannel();
                    callbackHome(null, show);
                })
                .show());

        adapter = new ItemAdapter(show, newData -> {
            show = newData;
            // 更新排序列表
            sortList = ConstantManager.getElementsBefore(newData, (Predicate<Bean>) item -> HIDE_CHANNEL_TITLE.equals(item.getTitle()));
        });
        mRecyclerView.setAdapter(adapter);

        // 设置 FlexboxLayoutManager 实现瀑布流
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        mRecyclerView.setLayoutManager(layoutManager);

        // 禁用默认动画避免闪烁
        ((SimpleItemAnimator) Objects.requireNonNull(mRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);

        // 获取数据库存储栏目分类
        allChannelsLive.observe(ChannelSheetFragment.this, channels -> {
            if (channels == null || channels.isEmpty()) {
                defaultChannel();
            } else {
                channels.sort(Comparator.comparing(Channel::getSortNo));
                List<Bean> temp = channels.stream().map(x -> {
                    Bean bean = new Bean();
                    bean.setShowClose(x.isShow());
                    bean.setItemType(Bean.TYPE_ITEM);
                    bean.setTitle(x.getCategoryName());
                    return bean;
                }).collect(Collectors.toList());
                Bean showTitle = new Bean();
                showTitle.setItemType(Bean.TYPE_GROUP);
                showTitle.setTitle(SHOW_CHANNEL_TITLE);
                temp.add(0, showTitle);

                OptionalInt firstHideIndex = IntStream.range(0, temp.size())
                        .filter(i -> temp.get(i).getItemType() == Bean.TYPE_ITEM && !temp.get(i).isShowClose())
                        .findFirst();
                Bean hideTitle = new Bean();
                hideTitle.setItemType(Bean.TYPE_GROUP);
                hideTitle.setTitle(HIDE_CHANNEL_TITLE);
                if (firstHideIndex.isPresent()) {
                    temp.add(firstHideIndex.getAsInt(), hideTitle);
                } else {
                    temp.add(temp.size(), hideTitle);
                }
                if (!show.isEmpty()) {
                    show.clear();
                }
                // 显示用的集合，把显示角标的状态关闭
                List<Bean> collect = temp.stream().peek(x -> x.setShowClose(false)).collect(Collectors.toList());
                show.addAll(collect);
                adapter.setBeanList(collect);
                adapter.notifyDataSetChanged();
            }
            // 获取显示 HIDE_CHANNEL_TITLE 之前的列表
            sortList = ConstantManager.getElementsBefore(adapter.getBeanList(), (Predicate<Bean>) item -> HIDE_CHANNEL_TITLE.equals(item.getTitle()));
            dropAndSort();
        });
    }

    private void callbackHome(List<Channel> channelSortList, List<Bean> show) {
        if (listener != null) {
            List<String> collect;
            if (show != null && !show.isEmpty()) {
                // 重置完后，所有的显示状态都为 false
                collect = show.stream().map(Bean::getTitle).collect(Collectors.toList());
            } else {
                collect = channelSortList.stream().filter(Channel::isShow).map(Channel::getCategoryName).collect(Collectors.toList());
            }

            if (mCategoriesSorted != null && !mCategoriesSorted.isEmpty()) {
                // 先将 mCategoriesSorted 转换为 Map 便于快速查找
                Map<String, Category> categoryMap = new HashMap<>();
                for (Category category : mCategoriesSorted) {
                    categoryMap.put(category.getName(), category);
                }

                // 按照 collect 的顺序构建结果
                List<Category> result = new ArrayList<>();
                for (String id : collect) {
                    Category category = categoryMap.get(id);
                    if (category != null) {
                        result.add(category);
                    }
                }
                requireActivity().runOnUiThread(() -> {
                    // 转换 Channel -> Category（根据你的逻辑）
                    listener.onBottomSheetConfirmed(result);
                });
                dismiss();
            }
        }
    }

    /**
     * <ol>
     *     <li>第一次打开栏目分类</li>
     *     <li>恢复默认设置后的栏目分类</li>
     * </ol>
     */
    @SuppressLint("NotifyDataSetChanged")
    private void defaultChannel() {
        Bean title = new Bean();
        title.setItemType(Bean.TYPE_GROUP);
        title.setTitle(SHOW_CHANNEL_TITLE);
        show.add(title);

        List<Bean> follow = mCategories.stream().map(x -> {
            Bean bean = new Bean();
            bean.setItemType(Bean.TYPE_ITEM);
            bean.setTitle(x.getName());
            return bean;
        }).collect(Collectors.toList());
        show.addAll(follow);

        Bean more = new Bean();
        more.setItemType(Bean.TYPE_GROUP);
        more.setTitle(HIDE_CHANNEL_TITLE);
        show.add(more);
        adapter.setBeanList(show);
        adapter.notifyDataSetChanged();
    }

    private void dropAndSort() {
        if (touchHelper != null) {
            touchHelper = null;
        }
        touchHelper = getItemTouchHelper(isEdit);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 再次打开时
        mEdit.setChecked(false);
        editStatus = false;
    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper(AtomicBoolean isEdit) {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (!isEdit.get()) {
                    return false;
                }
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (toPosition > 0 && fromPosition != toPosition && toPosition < sortList.size()) {
                    // 移动的范围只能是显示分类的范围内
                    Collections.swap(sortList, fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    show = ConstantManager.replaceOrder(show, sortList);
                    editStatus = true;
                    changed = true;
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 不启用滑动删除
            }
        };
        return new ItemTouchHelper(callback);
    }

    private void editFlag() {
        isEdit.set(true);
        mRest.setVisibility(View.VISIBLE);
        mEdit.setText("保存");
        mEdit.setChecked(true);
        editStatus = true;
        changed = false;
    }

    @Transaction
    private void updateSort(List<Channel> channelSortList) {
        List<Channel> channels = allChannelsLive.getValue();
        if (channels != null && !channels.isEmpty()) {
            mViewModel.updateAll(channelSortList, new ChannelRepository.UpdateCallback() {
                @Override
                public void onSuccess() {
                    Log.d("Update", "finish size " + channelSortList.size());
                    callbackHome(channelSortList, null);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Update", "error " + channelSortList.size());
                }
            });
        } else {
            mViewModel.insertAll(channelSortList);
            dismiss();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void editBehavior() {
        editFlag();
        if (!adapter.getBeanList().isEmpty()) {
            List<String> collect = adapter.getBeanList().stream().map(Bean::getTitle).collect(Collectors.toList());
            int lastIndex = collect.lastIndexOf(HIDE_CHANNEL_TITLE);
            for (int i = 0; i < lastIndex; i++) {
                Bean bean = adapter.getBeanList().get(i);
                if (bean.getItemType() == Bean.TYPE_ITEM) {
                    bean.setShowClose(true);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnDataChangedListener {
        void onDataChanged(List<Bean> newData);
    }

    // RecyclerView Adapter
    static class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Setter
        @Getter
        public List<Bean> beanList;
        private final OnDataChangedListener listener;
        public static final int VIEW_TITLE = 0;
        public static final int VIEW_ITEM = 1;

        public ItemAdapter(List<Bean> data, OnDataChangedListener listener) {
            this.beanList = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView;
            RecyclerView.ViewHolder holder;
            if (viewType == VIEW_TITLE) {
                itemView = inflater.inflate(R.layout.item_chanel_title, parent, false);
                holder = new TitleViewHolder(itemView);
            } else {
                itemView = inflater.inflate(R.layout.item_channel_chips, parent, false);
                holder = new ItemViewHolder(itemView);
            }
            return holder;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Bean item = beanList.get(position);
            if (holder instanceof TitleViewHolder) {
                TitleViewHolder viewHolder = (TitleViewHolder) holder;
                viewHolder.mTitle.setText(item.getTitle());
            } else if (holder instanceof ItemViewHolder) {
                ItemViewHolder viewHolder = (ItemViewHolder) holder;
                viewHolder.channelName.setText(item.getTitle());
                viewHolder.channelName.setCheckable(true);
//                viewHolder.channelName.setCloseIconVisible(beanList.get(position).isShowClose());
                if (beanList.get(position).isShowClose()) {
                    viewHolder.channelName.setCloseIconVisible(true);
                }
                if (viewHolder.channelName.isCloseIconVisible()) {
                    viewHolder.channelName.setCheckable(false);
                }
                // close icon click
//                viewHolder.channelName.setOnCloseIconClickListener(v -> {
//                    int hideTitleIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(HIDE_CHANNEL_TITLE));
//                    if (hideTitleIndex <= 2) {
//                        Toast.makeText(v.getContext(), "至少要有一个分类", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    Bean remove = beanList.remove(position);
//                    if (remove.isShowClose()) {
//                        remove.setShowClose(false);
//                        beanList.add(remove);
//                        changed = true;
//
//                        listener.onDataChanged(beanList);
//                    }
//                    notifyDataSetChanged();
//                });

                viewHolder.channelName.setOnClickListener(v -> {
                    if (editStatus) {
                        // 隐藏 --> 显示
                        if (!viewHolder.channelName.isCloseIconVisible()) {
                            int originIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(item.getTitle()));
                            notifyItemRemoved(originIndex);
                            Bean bean = beanList.stream().filter(x -> x.getTitle().equals(item.getTitle())).findFirst().get();
                            beanList.remove(bean);

                            int lastIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(HIDE_CHANNEL_TITLE));
                            item.setShowClose(true);
                            beanList.add(lastIndex, item);
                            changed = true;
                            notifyDataSetChanged();
                            listener.onDataChanged(beanList);
                        } else {
                            // 显示 --> 隐藏
                            int hideTitleIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(HIDE_CHANNEL_TITLE));
                            if (hideTitleIndex <= 2) {
                                Toast.makeText(v.getContext(), "至少要有一个分类", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Bean remove = beanList.remove(position);
                            if (remove.isShowClose()) {
                                remove.setShowClose(false);
                                beanList.add(remove);
                                changed = true;

                                listener.onDataChanged(beanList);
                            } else {
                                changed = false;
                            }
                        }
                    } else {
                        changed = false;
                    }
                    notifyDataSetChanged();
                });

//                if (editStatus && !viewHolder.channelName.isCloseIconVisible()) {
//                    viewHolder.channelName.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                        int originIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(item.getTitle()));
//                        notifyItemRemoved(originIndex);
//                        Bean bean = beanList.stream().filter(x -> x.getTitle().equals(item.getTitle())).findFirst().get();
//                        beanList.remove(bean);
//
//                        int lastIndex = ConstantManager.findLastIndex(beanList, x -> x.getTitle().equals(HIDE_CHANNEL_TITLE));
//                        item.setShowClose(true);
//                        beanList.add(lastIndex, item);
//
//                        notifyDataSetChanged();
//                        listener.onDataChanged(beanList);
//                    });
//                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (beanList.get(position).getItemType() == Bean.TYPE_GROUP) {
                return VIEW_TITLE;
            } else if (beanList.get(position).getItemType() == Bean.TYPE_ITEM) {
                return VIEW_ITEM;
            }
            return VIEW_ITEM;
        }

        @Override
        public int getItemCount() {
            return beanList.size();
        }

        static class ItemViewHolder extends RecyclerView.ViewHolder {
            Chip channelName;

            public ItemViewHolder(View itemView) {
                super(itemView);
                channelName = itemView.findViewById(R.id.chip_name);
            }
        }

        public static class TitleViewHolder extends RecyclerView.ViewHolder {
            TextView mTitle;

            public TitleViewHolder(@NonNull View itemView) {
                super(itemView);
                mTitle = itemView.findViewById(R.id.tv_group);
            }
        }

    }
}
