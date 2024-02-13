package mdteam.ait.tardis.control.impl;

import mdteam.ait.tardis.control.Control;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import mdteam.ait.tardis.Tardis;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import static mdteam.ait.tardis.data.DoorData.useDoor;

public class DoorControl extends Control {

    private SoundEvent soundEvent = SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;

    public DoorControl() {
        super("door_control");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world) {

        if(tardis.getHandlers().getSequenceHandler().hasActiveSequence()) {
            this.addToControlSequence(tardis);
            if(tardis.getHandlers().getSequenceHandler().controlPartOfSequence(this))
                return false;
        }

        this.soundEvent = !tardis.getDoor().isOpen() ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
        useDoor(tardis, world, player.getBlockPos(), player);
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }
}