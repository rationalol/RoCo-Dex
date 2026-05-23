package com.yinpei.rocodex.data.model

enum class Nature(val label: String, val up: StatType, val down: StatType) {
    // 增加生命
    ROBUST("强健", StatType.HP, StatType.ATK),
    LIVELY("活泼", StatType.HP, StatType.DEF),
    SIMPLE("淳朴", StatType.HP, StatType.MAT),
    GENEROUS("宽厚", StatType.HP, StatType.MDF),
    DULL("迟钝", StatType.HP, StatType.SPD),

    // 减少生命
    IRRITABLE("暴躁", StatType.ATK, StatType.HP),
    STUBBORN("顽固", StatType.DEF, StatType.HP),
    GLOOMY("阴沉", StatType.MAT, StatType.HP),
    ALOOF("孤高", StatType.MDF, StatType.HP),
    RADICAL("急进", StatType.SPD, StatType.HP),

    LONELY("孤僻", StatType.ATK, StatType.DEF),
    ADAMANT("固执", StatType.ATK, StatType.MAT),
    NAUGHTY("调皮", StatType.ATK, StatType.MDF),
    BRAVE("勇敢", StatType.ATK, StatType.SPD),

    BOLD("大胆", StatType.DEF, StatType.ATK),
    IMPISH("淘气", StatType.DEF, StatType.MAT),
    LAX("无虑", StatType.DEF, StatType.MDF),
    RELAXED("悠闲", StatType.DEF, StatType.SPD),

    MODEST("保守", StatType.MAT, StatType.ATK),
    MILD("稳重", StatType.MAT, StatType.DEF),
    RASH("马虎", StatType.MAT, StatType.MDF),
    QUIET("冷静", StatType.MAT, StatType.SPD),

    CALM("沉着", StatType.MDF, StatType.ATK),
    GENTLE("温顺", StatType.MDF, StatType.DEF),
    CAREFUL("慎重", StatType.MDF, StatType.MAT),
    SASSY("狂妄", StatType.MDF, StatType.SPD),

    TIMID("胆小", StatType.SPD, StatType.ATK),
    HASTY("急躁", StatType.SPD, StatType.DEF),
    JOLLY("开朗", StatType.SPD, StatType.MAT),
    NAIVE("天真", StatType.SPD, StatType.MDF);

    companion object {
        fun fromLabel(label: String?): Nature? {
            return entries.find { it.label == label }
        }
    }
}
