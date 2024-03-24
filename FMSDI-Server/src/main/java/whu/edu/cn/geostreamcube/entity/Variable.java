package whu.edu.cn.geostreamcube.entity;

/**
 * @author Ruixiang Liu
 */
public class Variable {
    private int variableKey;
    private String variableName;
    private String variableDescription;
    private String unit;

    public int getVariableKey() {
        return variableKey;
    }

    public void setVariableKey(int variableKey) {
        this.variableKey = variableKey;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableDescription() {
        return variableDescription;
    }

    public void setVariableDescription(String variableDescription) {
        this.variableDescription = variableDescription;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "variableKey=" + variableKey +
                ", variableName='" + variableName + '\'' +
                ", variableDescription='" + variableDescription + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
