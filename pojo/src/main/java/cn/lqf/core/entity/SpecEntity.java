package cn.lqf.core.entity;

import cn.lqf.core.pojo.specification.Specification;
import cn.lqf.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

public class SpecEntity implements Serializable {
    // 1  规格的实体
    private Specification specification;
    // 2 规格选项
    private List<SpecificationOption> specificationOptionList;

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    public SpecEntity() {
    }

    public SpecEntity(Specification specification, List<SpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }
}
