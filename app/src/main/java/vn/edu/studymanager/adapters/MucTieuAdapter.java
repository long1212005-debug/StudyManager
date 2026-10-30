package vn.edu.studymanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.studymanager.R;
import vn.edu.studymanager.model.entity.MucTieuDuDoan;

public class MucTieuAdapter extends RecyclerView.Adapter<MucTieuAdapter.ViewHolder> {

    private final Context context;
    private List<MucTieuDuDoan> list;

    public MucTieuAdapter(Context context, List<MucTieuDuDoan> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<MucTieuDuDoan> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_muc_tieu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MucTieuDuDoan item = list.get(position);
        holder.tvDiemChu.setText(item.getDiemChu());
        holder.tvMucTieu10.setText(String.format("Hệ 10: >= %.1f", item.getDiemHe10ToiThieu()));
        holder.tvMucTieu4.setText(String.format("GPA hệ 4: %.1f", item.getDiemHe4()));
        holder.tvKetQuaThi.setText(item.getTrangThaiMoTa());

        if (item.isDaDat()) {
            holder.tvKetQuaThi.setTextColor(ContextCompat.getColor(context, R.color.status_info));
            holder.tvDiemChu.setBackgroundColor(ContextCompat.getColor(context, R.color.status_info));
        } else if (!item.isKhaThi()) {
            holder.tvKetQuaThi.setTextColor(ContextCompat.getColor(context, R.color.status_error));
            holder.tvDiemChu.setBackgroundColor(ContextCompat.getColor(context, R.color.status_error));
        } else {
            if (item.getDiemThiCanDat() > 8.0f) {
                holder.tvKetQuaThi.setTextColor(ContextCompat.getColor(context, R.color.status_warning));
                holder.tvDiemChu.setBackgroundColor(ContextCompat.getColor(context, R.color.status_warning));
            } else {
                holder.tvKetQuaThi.setTextColor(ContextCompat.getColor(context, R.color.status_success));
                holder.tvDiemChu.setBackgroundColor(ContextCompat.getColor(context, R.color.status_success));
            }
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiemChu, tvMucTieu10, tvMucTieu4, tvKetQuaThi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiemChu = itemView.findViewById(R.id.tvDiemChu);
            tvMucTieu10 = itemView.findViewById(R.id.tvMucTieu10);
            tvMucTieu4 = itemView.findViewById(R.id.tvMucTieu4);
            tvKetQuaThi = itemView.findViewById(R.id.tvKetQuaThi);
        }
    }
}
