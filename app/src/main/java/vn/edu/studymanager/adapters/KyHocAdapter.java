package vn.edu.studymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.studymanager.R;
import vn.edu.studymanager.model.entity.KyHoc;

public class KyHocAdapter extends RecyclerView.Adapter<KyHocAdapter.ViewHolder> {

    public interface OnKyHocActionListener {
        void onItemClick(KyHoc kyHoc);
        void onEditClick(KyHoc kyHoc);
        void onDeleteClick(KyHoc kyHoc);
    }

    private final Context context;
    private List<KyHoc> list;
    private final OnKyHocActionListener listener;

    public KyHocAdapter(Context context, List<KyHoc> list, OnKyHocActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<KyHoc> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ky_hoc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KyHoc item = list.get(position);

        holder.tvTenKy.setText(item.getTenKy());
        holder.tvTinChiKy.setText(item.getTongTinChiKy() + " tín chỉ đăng ký");
        holder.tvGpaKy.setText(String.format("%.2f / 4.0", item.getGpaKy()));

        if (item.isTrangThai()) {
            holder.tvTrangThaiKy.setText("✅ [ĐÃ XONG]");
            holder.tvTrangThaiKy.setTextColor(ContextCompat.getColor(context, R.color.status_success));
            holder.tvTrangThaiKy.setBackgroundColor(ContextCompat.getColor(context, R.color.status_success_bg));
        } else {
            holder.tvTrangThaiKy.setText("⏳ [ĐANG HỌC]");
            holder.tvTrangThaiKy.setTextColor(ContextCompat.getColor(context, R.color.status_info));
            holder.tvTrangThaiKy.setBackgroundColor(ContextCompat.getColor(context, R.color.status_info_bg));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnMenuKyHoc.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenuKyHoc);
            popup.getMenu().add(0, 1, 0, "✏️ Chỉnh sửa kỳ học");
            popup.getMenu().add(0, 2, 1, "🗑️ Xóa kỳ học");

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == 1) {
                    if (listener != null) listener.onEditClick(item);
                    return true;
                } else if (menuItem.getItemId() == 2) {
                    if (listener != null) listener.onDeleteClick(item);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenKy, tvTinChiKy, tvGpaKy, tvTrangThaiKy;
        ImageView btnMenuKyHoc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKy = itemView.findViewById(R.id.tvTenKy);
            tvTinChiKy = itemView.findViewById(R.id.tvTinChiKy);
            tvGpaKy = itemView.findViewById(R.id.tvGpaKy);
            tvTrangThaiKy = itemView.findViewById(R.id.tvTrangThaiKy);
            btnMenuKyHoc = itemView.findViewById(R.id.btnMenuKyHoc);
        }
    }
}
