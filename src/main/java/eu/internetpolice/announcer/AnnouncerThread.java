package eu.internetpolice.announcer;

import java.util.Random;

public class AnnouncerThread extends Thread
{
    private final Random randomGenerator;
    private final Announcer plugin;
    private int lastAnnouncement;
    
    public AnnouncerThread(final Announcer plugin) {
        this.randomGenerator = new Random();
        this.lastAnnouncement = 0;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        if (this.plugin.isAnnouncerEnabled()) {
            if (this.plugin.isRandom()) {
                this.lastAnnouncement = Math.abs(this.randomGenerator.nextInt() % this.plugin.numberOfAnnouncements());
            }
            else if (++this.lastAnnouncement >= this.plugin.numberOfAnnouncements()) {
                this.lastAnnouncement = 0;
            }
            if (this.lastAnnouncement < this.plugin.numberOfAnnouncements()) {
                this.plugin.announce(this.lastAnnouncement + 1);
            }
        }
    }
}
