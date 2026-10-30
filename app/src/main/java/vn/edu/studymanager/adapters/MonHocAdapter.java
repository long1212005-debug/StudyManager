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
import vn.edu.studymanager.model.entity.MonHoc;

public class MonHocAdapter extends RecyclerView.Adapter<MonHocAdapter.ViewHolder> {

    public interface OnMonHocActionListener {
        void onItemClick(MonHoc monHoc);
        void onEditScoreClick(MonHoc monHoc);
        void onCalculateExamClick(MonHoc monHoc);
        void onDeleteClick(MonHoc monHoc);
    }

    private final Context context;
    private List<MonHoc> list;
    private final OnMonHocActionListener listener;

    public MonHocAdapter(Context context, List<MonHoc> list, OnMonHocActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<MonHoc> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_hoc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonHoc item = list.get(position);

        holder.tvTenMon.setText(item.getTenMon());
        holder.tvSoTinChi.setText(item.getSoTinChi() + " tín chỉ");

        // Format điểm thành phần
        StringBuilder tpBuilder = new StringBuilder("Điểm TX: ");
        tpBuilder.append("TX1: ").append(item.getDiemTx1() > 0 ? String.valueOf(item.getDiemTx1()) : "--");
        tpBuilder.append(" | TX2: ").append(item.getDiemTx2() > 0 ? String.valueOf(item.getDiemTx2()) : "--");
        if (item.getDiemTx3() != null) {
            tpBuilder.append(" | TX3: ").append(item.getDiemTx3() > 0 ? String.valueOf(item.getDiemTx3()) : "--");
        }
        holder.tvDiemThanhPhan.setText(tpBuilder.toString());

        // Điểm thi
        if (item.getDiemThi() > 0) {
            holder.tvDiemThi.setText(String.format("Thi: %.1f", item.getDiemThi()));
        } else {
            holder.tvDiemThi.setText("Thi: Chưa có điểm");
        }

        // Điểm tổng kết
        if (item.getDiemChu() != null && !item.getDiemChu().isEmpty()) {
            holder.tvDiemTongKet.setText(String.format("TK10: %.1f | %s (%.1f)",
                    item.getDiemTongKet10(), item.getDiemChu(), item.getDiemTongKet4()));
        } else {
            holder.tvDiemTongKet.setText("Chưa tổng kết");
        }

        // Trạng thái môn (UI 3: Không chỉ màu sắc mà có Icon + Nhãn rõ ràng)
        String trangThai = item.getTrangThai();
        if ("Qua môn".equalsIgnoreCase(trangThai)) {
            holder.tvTrangThaiMon.setText("✅ [QUA MÔN]");
            holder.tvTrangThaiMon.setTextColor(ContextCompat.getColor(context, R.color.status_success));
            holder.tvTrangThaiMon.setBackgroundColor(ContextCompat.getColor(context, R.color.status_success_bg));
        } else if ("Trượt".equalsIgnoreCase(trangThai) || "Học lại".equalsIgnoreCase(trangThai)) {
            holder.tvTrangThaiMon.setText("❌ [TRƯỢT MÔN]");
            holder.tvTrangThaiMon.setTextColor(ContextCompat.getColor(context, R.color.status_error));
            holder.tvTrangThaiMon.setBackgroundColor(ContextCompat.getColor(context, R.color.status_error_bg));
        } else {
            holder.tvTrangThaiMon.setText("⏳ [ĐANG HỌC]");
            holder.tvTrangThaiMon.setTextColor(ContextCompat.getColor(context, R.color.status_info));
            holder.tvTrangThaiMon.setBackgroundColor(ContextCompat.getColor(context, R.color.status_info_bg));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnMenuMonHoc.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenuMonHoc);
            popup.getMenu().add(0, 1, 0, "✏️ Nhập / Sửa điểm môn học");
            popup.getMenu().add(0, 2, 1, "🎯 Tính điểm thi cần đạt");
            popup.getMenu().add(0, 3, 2, "🗑️ Xóa môn học");

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == 1) {
                    if (listener != null) listener.onEditScoreClick(item);
                    return true;
                } else if (menuItem.getItemId() == 2) {
                    if (listener != null) listener.onCalculateExamClick(item);
                    return true;
                } else if (menuItem.getItemId() == 3) {
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
        TextView tvTenMon, tvSoTinChi, tvDiemThanhPhan, tvDiemThi, tvDiemTongKet, tvTrangThaiMon;
        ImageView btnMenuMonHoc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvSoTinChi = itemView.findViewById(R.id.tvSoTinChi);
            tvDiemThanhPhan = itemView.findViewById(R.id.tvDiemThanhPhan);
            tvDiemThi = itemView.findViewById(R.id.tvDiemThi);
            tvDiemTongKet = itemView.findViewById(R.id.tvDiemTongKet);
            tvTrangThaiMon = itemView.findViewById(R.id.tvTrangThaiMon);
            btnMenuMonHoc = itemView.findViewById(R.id.btnMenuMonHoc);
        }
    }
}
