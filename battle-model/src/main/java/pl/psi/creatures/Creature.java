package pl.psi.creatures;
//  ******************************************************************
//
//  Copyright 2022 PSI Software AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
//  ******************************************************************

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import lombok.Setter;
import pl.psi.TurnQueue;

import com.google.common.collect.Range;

import lombok.Getter;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
@Getter
@Setter
public class Creature implements PropertyChangeListener
{
    private CreatureStatisticIf basicStats;
    private CreatureStats externalStats = new CreatureStats.CreatureStatsBuilder().build();
    private CreatureStats buffedStats = new CreatureStats.CreatureStatsBuilder().build();
    private int amount;
    private double currentHp;
    private boolean canCounterAttack = true;
    private DamageCalculatorIf calculator;

Creature()
{
}

    private Creature( final CreatureStatisticIf aStats, final DamageCalculatorIf aCalculator,
                      final int aAmount )
    {
        basicStats = aStats;
        amount = aAmount;
        currentHp = basicStats.getMaxHp();
        calculator = aCalculator;
    }

    public void increaseStats( CreatureStatisticIf statIncrease ){
        externalStats.addStats( statIncrease );
    }

    public void attack( final Creature aDefender )
    {
        if( isAlive() )
        {
            final int damage = getCalculator().calculateDamage( this, aDefender );
            applyDamage( aDefender, damage );
            if( canCounterAttack( aDefender ) )
            {
                counterAttack( aDefender );
            }
        }
    }


    public boolean isAlive()
    {
        return getAmount() > 0;
    }

    protected void applyDamage( final Creature aDefender, final int aDamage )
    {
        aDefender.setCurrentHp( (aDefender.getCurrentHp() - aDamage));
    }

    protected void setCurrentHp( final double aCurrentHp )
    {
        currentHp = aCurrentHp;
    }

    public void age(){
        CreatureStats reduceMaxHp = new CreatureStats
                .CreatureStatsBuilder()
                .maxHp( -(getStats().getMaxHp() / 2) )
                .build();
        buff( reduceMaxHp );
        final double currentHpAfterAge = Math.max(getCurrentHp() - ( getBasicStats().getMaxHp() - getStats().getMaxHp() ), 1);
        setCurrentHp( currentHpAfterAge );
    }

    private void calculateUnits(final double aAmountToAdd){
        if (aAmountToAdd > 1){
            amount += aAmountToAdd - 1;
        }
        else{
            amount += aAmountToAdd;
        }
    }

    protected void heal( double healAmount )
    {
        setCurrentHp( (getCurrentHp() + healAmount));
        calculateUnits( calculateAmount() );
        setCurrentHp( calculateCurrentHp() );
    }

    private double calculateAmount(){
        if ( getCurrentHp() / getStats().getMaxHp() == 1 ){
            return 1;
        }
        else if ( getCurrentHp() % getStats().getMaxHp() == 0 ){
            return (int)(getCurrentHp() / getStats().getMaxHp());
        }
        else{
            return (int)((getCurrentHp() / getStats().getMaxHp()) + 1);
        }
    }

    private double calculateCurrentHp(){
        if( getCurrentHp() - ( getAmount() * getStats().getMaxHp() ) == 0 ){
            return (int)getStats().getMaxHp();
        }
        else{ // ( a % b + b ) % b == a % b   when a is negative % operator behaves funky
            return (int)(( ( getCurrentHp() - ( getAmount() * getStats().getMaxHp() ) ) % ( getStats().getMaxHp() ) + ( getStats().getMaxHp() ) ) % getStats().getMaxHp());
        }
    }

    protected boolean canCounterAttack( final Creature aDefender )
    {
        return aDefender.canCounterAttack && aDefender.getCurrentHp() > 0;
    }

    protected void counterAttack( final Creature aAttacker )
    {
        final int damage = aAttacker.getCalculator()
                .calculateDamage( aAttacker, this );
        applyDamage( this, damage );
        aAttacker.canCounterAttack = false;
    }

    public void buff( CreatureStatisticIf statsToAdd ){
        buffedStats.addStats( statsToAdd );
    }

    public CreatureStatisticIf getStats(){
        CreatureStats stats = new CreatureStats.CreatureStatsBuilder().build();
        stats.addStats( basicStats );
        stats.addStats( externalStats );
        stats.addStats( buffedStats );
        return stats;
    }

    Range< Integer > getDamage()
    {
        return getStats().getDamage();
    }

    double getMaxHp(){
        return getStats().getMaxHp();
    }

    double getAttack()
    {
        return getStats().getAttack();
    }

    double getArmor()
    {
        return getStats().getArmor();
    }

    @Override
    public void propertyChange( final PropertyChangeEvent evt )
    {
        if( TurnQueue.END_OF_TURN.equals( evt.getPropertyName() ) )
        {
            canCounterAttack = true;
        }
    }

    protected void restoreCurrentHpToMax()
    {
        currentHp = getStats().getMaxHp();
    }

    public String getName()
    {
        return getStats().getName();
    }

    public double getMoveRange()
    {
        return getStats().getMoveRange();
    }

    public static class Builder
    {
        private int amount = 1;
        private DamageCalculatorIf calculator = new DefaultDamageCalculator( new Random() );
        private CreatureStatisticIf statistic;

        public Builder statistic( final CreatureStatisticIf aStatistic )
        {
            statistic = aStatistic;
            return this;
        }

        public Builder amount( final int aAmount )
        {
            amount = aAmount;
            return this;
        }

        Builder calculator( final DamageCalculatorIf aCalc )
        {
            calculator = aCalc;
            return this;
        }


        public Creature build()
        {
            return new Creature( statistic, calculator, amount );
        }
    }
}
