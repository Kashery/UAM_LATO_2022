package pl.psi.creatures;

public class BonusDamageToDefenderType extends AbstractCreature {
    private CreatureStatistic strongType;
    private final double strongMultiplier;
    public BonusDamageToDefenderType(Creature aDecorated, CreatureStatistic type, final double multiplier) {
        super(aDecorated);
        strongType = type;
        strongMultiplier = multiplier;
    }

    @Override
    public void attack(Creature aDefender) {
        if(aDefender.getBasicStats().equals(strongType)){
            super.attack(aDefender, strongMultiplier);
        }else{
            super.attack(aDefender);
        }

    }
}
