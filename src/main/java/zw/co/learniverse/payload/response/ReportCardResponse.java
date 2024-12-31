package zw.co.learniverse.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;
import java.util.UUID;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ReportCardResponse {

        private Long id;
        private Long userId;
        private String firstName;
        private String lastName;
        private String middleName;
        private String email;
        private Long schoolId;
        private UUID classId;
        private UUID levelId;
        private UUID termId;
        private Year year;
        private List<ReportSubResponse> subs;

    }

