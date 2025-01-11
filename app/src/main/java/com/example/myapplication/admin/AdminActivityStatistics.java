package com.example.myapplication.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.database.OrderDatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AdminActivityStatistics extends AppCompatActivity {

    private BarChart barChart;
    private Spinner spinner;
    private OrderDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_statistics);

        // 1. Ánh xạ
        barChart = findViewById(R.id.barChart);
        spinner = findViewById(R.id.spinner_timeframe);

        databaseHelper = new OrderDatabaseHelper(this);
        databaseHelper.open();

        // 2. Xử lý sự kiện Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Theo ngày
                        loadDailyData();
                        break;
                    case 1: // Theo tháng
                        loadMonthlyData();
                        break;
                    case 2: // Theo năm
                        loadYearlyData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void loadDailyData() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        List<Map<String, Object>> topProducts = databaseHelper.getTopSellingProducts("day", today);
        showBarChart(topProducts);
    }

    private void loadMonthlyData() {
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(Calendar.getInstance().getTime());
        List<Map<String, Object>> topProducts = databaseHelper.getTopSellingProducts("month", currentMonth);
        showBarChart(topProducts);
    }

    private void loadYearlyData() {
        String currentYear = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
        List<Map<String, Object>> topProducts = databaseHelper.getTopSellingProducts("year", currentYear);
        showBarChart(topProducts);
    }

    private void showBarChart(List<Map<String, Object>> topProducts) {
        // 1. Chuẩn bị dữ liệu
        List<String> labels = new ArrayList<>();
        List<BarEntry> quantityEntries = new ArrayList<>();
        List<BarEntry> revenueEntries = new ArrayList<>();

        for (int i = 0; i < topProducts.size(); i++) {
            Map<String, Object> product = topProducts.get(i);
            String productName = (String) product.get("product_name");
            int totalQuantity = (int) product.get("total_quantity");
            float totalRevenue = ((Number) product.get("total_revenue")).floatValue();

            labels.add(productName);
            quantityEntries.add(new BarEntry(i, totalQuantity));
            revenueEntries.add(new BarEntry(i, totalRevenue));
        }

        // 2. Tạo BarDataSet cho số lượng và tổng tiền
        BarDataSet quantityDataSet = new BarDataSet(quantityEntries, "Số lượng bán ra");
        quantityDataSet.setColor(getResources().getColor(R.color.teal_200));

        BarDataSet revenueDataSet = new BarDataSet(revenueEntries, "Tổng tiền");
        revenueDataSet.setColor(getResources().getColor(R.color.purple_500));

        // 3. Tạo BarData
        BarData barData = new BarData(quantityDataSet, revenueDataSet);
        barData.setValueTextSize(12f);
        barData.setBarWidth(0.4f);

        // 4. Cấu hình trục X
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);

        // 5. Cấu hình BarChart
        barChart.getAxisLeft().setAxisMinimum(0f); // Đảm bảo trục Y bắt đầu từ 0
        barChart.getAxisRight().setEnabled(false); // Ẩn trục Y bên phải
        barChart.getDescription().setEnabled(false); // Ẩn mô tả biểu đồ

        // 6. Gán dữ liệu vào BarChart
        barChart.setData(barData);
        barChart.groupBars(-0.5f, 0.1f, 0.05f); // Nhóm các cột lại
        barChart.invalidate();
    }
}
