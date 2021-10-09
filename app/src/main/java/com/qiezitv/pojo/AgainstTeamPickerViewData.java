package com.qiezitv.pojo;

import com.contrarywind.interfaces.IPickerViewData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgainstTeamPickerViewData implements IPickerViewData {
    private String hostTeamName;
    private String guestTeamName;
    private String hostTeamHeadImg;
    private String guestTeamHeadImg;
    private String score;
    private String againstTeam;
    private Integer againstIndex;

    @Override
    public String getPickerViewText() {
        return this.againstTeam;
    }
}
