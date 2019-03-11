package cn.edu.seu.kse.lpmln.model;

import cn.edu.seu.kse.lpmln.app.LPMLNApp;
import cn.edu.seu.kse.lpmln.exception.solveexception.AssignConflictException;
import cn.edu.seu.kse.lpmln.translator.impl.LPMLN2ASPTranslator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 许鸿翔
 * @date 2018/1/17
 */
public class AugmentedSubset implements Cloneable{
    private static final String NOT = "not ";
    private static final String RULEEND = ".\r\n";
    protected Set<Integer> satIdx;
    protected Set<Integer> unsatIdx;
    protected Set<Integer> unknownIdx;
    protected LpmlnProgram lpmlnProgram;
    public AugmentedSubset(LpmlnProgram lpmlnProgram){
        satIdx = new HashSet<>();
        unsatIdx = new HashSet<>();
        unknownIdx = new HashSet<>();
        List<Rule> rules = lpmlnProgram.getRules();
        for(int i=0;i<rules.size();i++){
            if(LPMLNApp.SEMANTICS_STRONG.equals(LPMLNApp.semantics)&&rules.get(i).isSoft()&&!rules.get(i).isUnWeighted()){
                unknownIdx.add(i);
            }else{
                if(rules.get(i).isSoft()){
                    unknownIdx.add(i);
                }else{
                    satIdx.add(i);
                }
            }
        }
        this.lpmlnProgram = lpmlnProgram;
    }

    /**
     * 只用于clone
     */
    AugmentedSubset(){
    }

    public boolean sat(int idx){
        if(satIdx.contains(idx)){
            return false;
        }
        if(unknownIdx.contains(idx)){
            satIdx.add(idx);
            unknownIdx.remove(idx);
            return true;
        }
        throw new AssignConflictException("partition fail, Idx error");
    }

    public boolean unsat(int idx){
        if(unsatIdx.contains(idx)){
            return false;
        }
        if(unknownIdx.contains(idx)){
            unsatIdx.add(idx);
            unknownIdx.remove(idx);
            return true;
        }
        throw new AssignConflictException("partition fail, Idx error");
    }

    @Override
    public AugmentedSubset clone(){
        AugmentedSubset clone = new AugmentedSubset();
        clone.setLpmlnProgram(lpmlnProgram);
        clone.satIdx = new HashSet<>(satIdx);
        clone.unsatIdx = new HashSet<>(unsatIdx);
        clone.unknownIdx = new HashSet<>(unknownIdx);
        return clone;
    }

    public LpmlnProgram toLpmlnProgram(){
        LpmlnProgram aprogram = new LpmlnProgram();
        StringBuilder facts = new StringBuilder();
        List<Rule> originalRules = lpmlnProgram.getRules();
        List<Rule> aRules = new ArrayList<>();
        LPMLN2ASPTranslator aspTranslator = new LPMLN2ASPTranslator();
        aspTranslator.setProgram(lpmlnProgram);

        for(int i=0;i<originalRules.size();i++){
            Rule toadd = originalRules.get(i).clone();
            if(satIdx.contains(i)){
                toadd.setUnWeighted(true);
            }else if(unsatIdx.contains(i)){
                String unsatRule = aspTranslator.translateRuleUnsat(toadd);
                toadd.setOriginalrule(unsatRule);
                toadd.setUnWeighted(true);
            }
            aRules.add(toadd);
        }
        aprogram.setRules(aRules);
        aprogram.setMetarule(lpmlnProgram.getMetarule()+facts.toString());
        aprogram.setFactor(lpmlnProgram.getFactor());
        aprogram.setHerbrandUniverse(lpmlnProgram.getHerbrandUniverse());
        return aprogram;
    }

    public String getUnsatWeight(int i){
        Rule rule = lpmlnProgram.getRules().get(i);
        return ":~ #true.["+(rule.isSoft()?(int)rule.getWeight():1)+"@"+(rule.isSoft()?1:2)+","+i+"]\r\n";
    }

    public Set<Integer> getSatIdx() {
        return satIdx;
    }

    public void setSatIdx(Set<Integer> satIdx) {
        this.satIdx = satIdx;
    }

    public Set<Integer> getUnsatIdx() {
        return unsatIdx;
    }

    public void setUnsatIdx(Set<Integer> unsatIdx) {
        this.unsatIdx = unsatIdx;
    }

    public LpmlnProgram getLpmlnProgram() {
        return lpmlnProgram;
    }

    public void setLpmlnProgram(LpmlnProgram lpmlnProgram) {
        this.lpmlnProgram = lpmlnProgram;
    }

    public Set<Integer> getUnknownIdx() {
        return unknownIdx;
    }

    public void setUnknownIdx(Set<Integer> unknownIdx) {
        this.unknownIdx = unknownIdx;
    }

    @Override
    public String toString(){
        return "{"+unknownIdx+","+satIdx+","+unsatIdx+"}";
    }
}
