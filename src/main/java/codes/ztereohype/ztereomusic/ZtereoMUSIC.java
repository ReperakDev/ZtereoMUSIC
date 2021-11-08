package codes.ztereohype.ztereomusic;

import codes.ztereohype.ztereomusic.audio.GuildMusicPlayer;
import codes.ztereohype.ztereomusic.command.Command;
import codes.ztereohype.ztereomusic.command.commands.Ping;
import codes.ztereohype.ztereomusic.command.commands.Play;
import codes.ztereohype.ztereomusic.command.commands.Skip;
import codes.ztereohype.ztereomusic.database.Config;
import codes.ztereohype.ztereomusic.listeners.CommandListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.shadew.json.JsonSyntaxException;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

@Getter
@Setter(AccessLevel.PRIVATE)
public class ZtereoMUSIC {
    public static final ZtereoMUSIC INSTANCE = new ZtereoMUSIC();

    private final Map<String, Command> commandMap = new HashMap<>();
    private final Map<String, String> commandAliases = new HashMap<>();

    private Config config;
    private JDA jda;

    private AudioPlayerManager playerManager;
    private Map<Long, GuildMusicPlayer> guildMusicPlayerMap = new HashMap<>();

    private ZtereoMUSIC() {
    }

    public static ZtereoMUSIC getInstance() {
        return ZtereoMUSIC.INSTANCE;
    }

    @SneakyThrows({ JsonSyntaxException.class, FileNotFoundException.class, LoginException.class,
                    InterruptedException.class })
    public static void main(String[] args) {
        ZtereoMUSIC ztereoMUSIC = ZtereoMUSIC.getInstance();

        ztereoMUSIC.setConfig(Config.loadFrom("./config.json5"));
        ztereoMUSIC.setJda(JDABuilder.createDefault(ztereoMUSIC.getConfig().getPropreties().get("token"), GUILD_MESSAGES,
                                                    GUILD_VOICE_STATES).build().awaitReady());

        ztereoMUSIC.setupAudio();
        ztereoMUSIC.setCommands();
        ztereoMUSIC.setListeners();
    }

    private void setCommands() {
        Ping ping = new Ping();
        this.getCommandMap().put(ping.getMeta().getName(), ping);

        Play play = new Play();
        this.getCommandMap().put(play.getMeta().getName(), play);

        Skip skip = new Skip();
        this.getCommandMap().put(skip.getMeta().getName(), skip);

        for (String commandName : this.getCommandAliases().keySet()) {
            for (String aliasName : this.getCommandMap().get(commandName).getMeta().getAliases()) {
                this.getCommandAliases().put(aliasName, commandName);
            }
        }
    }

    private void setupAudio() {
        this.setPlayerManager(new DefaultAudioPlayerManager());
        AudioSourceManagers.registerRemoteSources(this.getPlayerManager());
        AudioSourceManagers.registerLocalSource(this.getPlayerManager());
    }

    private void setListeners() {
        this.getJda().addEventListener(new CommandListener());
    }
}
