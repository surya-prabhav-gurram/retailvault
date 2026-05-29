package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "dim_date", catalog = "retailvault_warehouse")
public class DimDate {
    @Id
    @Column(name = "date_key")
    private Integer dateKey;
    @Column(name = "full_date")
    private LocalDate fullDate;
    @Column(name = "day_of_week")
    private Integer dayOfWeek;
    @Column(name = "day_name")
    private String dayName;
    @Column(name = "day_of_month")
    private Integer dayOfMonth;
    @Column(name = "day_of_year")
    private Integer dayOfYear;
    @Column(name = "week_of_year")
    private Integer weekOfYear;
    @Column(name = "month_num")
    private Integer monthNum;
    @Column(name = "month_name")
    private String monthName;
    private Integer quarter;
    private Integer year;
    @Column(name = "is_weekend")
    private Boolean isWeekend;
    @Column(name = "is_holiday")
    private Boolean isHoliday;
}
