package org.demo.useraccounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.exceptions.ErrorCode;

import java.time.LocalDate;

@Builder(builderMethodName = "validateAndBuild")
@Getter
@Setter
public class DateRange {

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate from;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate to;


    public static DateRangeBuilder builder() {
        return new DateRangeBuilder(){
            @Override
            public DateRange build() {
                if(isValid())
                    return super.build();
                else
                    return null;
            }
        };


    }

    public static class DateRangeBuilder  {
        public boolean isValid() {
            if((this.from!=null && this.to!=null) || (this.from==null && this.to ==null))
                return true;

            throw new BaseRuntimeException("Invalid date range", ErrorCode.INVALID_REQUEST);
        }
    }

}
