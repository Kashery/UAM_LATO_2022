package pl.psi.creatures;

public class MultiRetaliationCreatureDecorator extends AbstractCreature{
    private final int retaliationCounter;
    private int currentRetaliation;
    public MultiRetaliationCreatureDecorator(Creature aDecorated, int retaliation) {
        super(aDecorated);
        retaliationCounter = retaliation;
        currentRetaliation = retaliationCounter;
        setCanCounterAttack(true);

    }
    @Override
    protected void counterAttack(final Creature aAttacker) {
        final int damage = getCalculator()
                .calculateDamage(this, aAttacker);
        applyCounterAttackDamage(aAttacker, damage);
        setLastCounterAttackDamage(damage);
        if (currentRetaliation == 1){
            currentRetaliation = retaliationCounter;
            setCanCounterAttack(false);
        }else{ currentRetaliation -=1; }
    }
    public int getCurrentRetaliation(){ return currentRetaliation; }

}
