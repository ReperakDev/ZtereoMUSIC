package codes.ztereohype.ztereomusic.command.commands;

import codes.ztereohype.ztereomusic.ZtereoMUSIC;
import codes.ztereohype.ztereomusic.audio.TrackManager;
import codes.ztereohype.ztereomusic.audio.TrackManagers;
import codes.ztereohype.ztereomusic.command.Command;
import codes.ztereohype.ztereomusic.command.CommandMeta;
import codes.ztereohype.ztereomusic.command.permissions.VoiceChecks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Disconnect implements Command {
    CommandMeta meta = new CommandMeta("disconnect", "A command to kick the bot from the vc.", new String[]{"fuckoff", "bye"}, false, false, new VoiceChecks[]{ VoiceChecks.BOT_CONNECTED, VoiceChecks.USER_CONNECTED, VoiceChecks.SAME_VC_IF_CONNECTED });

    @Override
    public CommandMeta getMeta() {
        return this.meta;
    }

    public void execute(MessageReceivedEvent messageEvent, String[] args) {
        Guild guild = messageEvent.getGuild();
        AudioManager audioManager = guild.getAudioManager();

        TrackManager trackManager = ZtereoMUSIC.getInstance().getGuildTrackManagerMap().get(guild.getIdLong());

        if (trackManager == null) {
            audioManager.closeAudioConnection();
            return;
        }

        TrackManagers.removeGuildTrackManager(guild);
    }
}
