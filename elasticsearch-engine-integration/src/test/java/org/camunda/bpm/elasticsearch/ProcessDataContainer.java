package org.camunda.bpm.elasticsearch;

import org.camunda.bpm.engine.runtime.VariableInstance;

import java.util.List;

public class ProcessDataContainer {

  private String pid;
  private String name;
  private List<VariableInstance> variables;

  public ProcessDataContainer() {}

  public ProcessDataContainer(String pid) {
    this.pid = pid;
  }

  public ProcessDataContainer(String pid, String name) {
    this(pid);
    this.name = name;
    this.variables = null;
  }

  public ProcessDataContainer(String pid, String name, List<VariableInstance> variables) {
    this(pid, name);
    this.variables = variables;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public List<VariableInstance> getVariables() {
    return variables;
  }

  public void setVariables(List<VariableInstance> variables) {
    this.variables = variables;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
