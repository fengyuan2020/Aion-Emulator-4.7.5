package quest.morheim;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vlog
 * @author Skunkworks
 */
public class _2041HoldTheFrontLine extends QuestHandler
{
	private final static int questId = 2041;
	private final static int[] npcIds = { 204301, 204403, 204432 };
	private final static int[] mobIds = { 280818, 211624, 213578, 213579 };

	public _2041HoldTheFrontLine()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int mob : mobIds)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);

		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnDie(questId);

		for (int npcId : npcIds)
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env)
	{
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env, 2300, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204301) { // Aegir
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 2375);

				return sendQuestEndDialog(env);
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				// Aegir
				case 204301: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
						case SETPRO1:
							changeQuestStep(env, 0, 1, false);
							TeleportService2.teleportTo(player, WorldMapType.MORHEIM.getId(), (float) 2795.3435, (float) 477.7972, (float) 265.49142, (byte) 47, TeleportAnimation.BEAM_ANIMATION);
							return closeDialogWindow(env);
					}
					break;
				}
				// Taisan
				case 204403: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case SETPRO2:
							changeQuestStep(env, 1, 2, false);
							TeleportService2.teleportTo(player, WorldMapType.MORHEIM.getId(), (float) 3027.3755, (float) 872.0247, (float) 362.875, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
							return closeDialogWindow(env);
					}
					break;
				}
				// Kargate
				case 204432: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							} else if (var == 4) {
								return sendQuestDialog(env, 2034);
							}
						case SETPRO3: {
							boolean areSpawned = false;
							if (player.isInGroup2()) {
								PlayerGroup playerGroup = player.getPlayerGroup2();
								for (Player p : playerGroup.getMembers()) {
									QuestState qs1 = p.getQuestStateList().getQuestState(questId);
									if (qs1 != null && qs1.getStatus() == QuestStatus.START && qs1.getQuestVarById(0) == 3)
										areSpawned = true;
								}
							}
							if (!areSpawned) {
								List<Npc> mobs = new ArrayList<Npc>();
								// Crusader (2)
								mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 211624, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
								mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 211624, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
								// Draconute Scout (2)
								mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
								mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));

								for (Npc mob : mobs) {
									mob.setTarget(player);
									((AbstractAI) mob.getAi2()).setStateIfNot(AIState.WALKING);
									mob.setState(1);
									mob.getMoveController().moveToTargetObject();
									PacketSendUtility.broadcastPacket(mob, new SM_EMOTION(mob, EmotionType.START_EMOTE2, 0, mob.getObjectId()));
								}
							}
							QuestService.questTimerStart(env, 240); // 4 minutes
							return defaultCloseDialog(env, 2, 3); // 3
						}
						case SETPRO4:
							if (var == 4) {
								changeQuestStep(env, 4, 4, true);
								closeDialogWindow(env);
								TeleportService2.teleportTo(player, WorldMapType.MORHEIM.getId(), 305.9068f, 2277.977f, 448.77292f, (byte)45, TeleportAnimation.BEAM_ANIMATION);
								return true;
							}
					}
					break;
				}
			}
		}

		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		if (var != 3) {
			return false;
		}

		int var1 = qs.getQuestVarById(1); // first flow
		int var2 = qs.getQuestVarById(2); // second flow
		int var3 = qs.getQuestVarById(3); // third flow
		int var4 = qs.getQuestVarById(4); // fourth flow
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 211624:
			case 280818:
				if ((var1 >= 0) && (var1 < 3)) {
					switch (targetId) {
						case 211624:
						case 280818:
							qs.setQuestVarById(1, var1 + 1); // 1: 1, 2, 3
							return true;
					}
				} else if (var1 == 3) {
					switch (targetId) {
						case 211624:
						case 280818:
							List<Npc> mobs = new ArrayList<Npc>();
							qs.setQuestVarById(1, 4); // 1: 4
							// Draconute Scout (2)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Crusader (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 211624, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Chandala Scaleguard (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213578, 254.21326f, 256.9302f, 226.6418f, (byte) 93));

							for (Npc mob : mobs)
								mob.getAggroList().addHate(player, 1);

							return true;
					}
				} else if (var1 == 4 && var2 >= 0 && var2 < 3) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
							qs.setQuestVarById(2, var2 + 1); // 2: 1, 2, 3
							return true;
					}
				} else if (var1 == 4 && var2 == 3) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
							List<Npc> mobs = new ArrayList<Npc>();
							qs.setQuestVarById(2, 4); // 2: 4
							// Draconute Scout (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Crusader (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 211624, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Chandala Scaleguard (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213578, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Chandala Fangblade (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213579, 254.21326f, 256.9302f, 226.6418f, (byte) 93));

							for (Npc mob : mobs)
								mob.getAggroList().addHate(player, 1);

							return true;
					}
				} else if (var1 == 4 && var2 == 4 && var3 >= 0 && var3 < 3) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
						case 213579:
							qs.setQuestVarById(3, var3 + 1); // 3: 1, 2, 3
							return true;
					}
				} else if (var1 == 4 && var2 == 4 && var3 == 3) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
						case 213579:
							List<Npc> mobs = new ArrayList<Npc>();
							qs.setQuestVarById(3, 4); // 3: 4
							// Draconute Scout (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 280818, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Crusader (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 211624, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Chandala Scaleguard (2)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213578, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213578, 254.21326f, 256.9302f, 226.6418f, (byte) 93));
							// Chandala Fangblade (1)
							mobs.add((Npc) QuestService.spawnQuestNpc(WorldMapType.NIDALBER.getId(), player.getInstanceId(), 213579, 254.21326f, 256.9302f, 226.6418f, (byte) 93));

							for (Npc mob : mobs)
								mob.getAggroList().addHate(player, 1);

							return true;
					}
				} else if (var1 == 4 && var2 == 4 && var3 == 4 && var4 >= 0 && var4 < 4) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
						case 213579:
							qs.setQuestVarById(4, var4 + 1); // 4: 1, 2, 3, 4
							return true;
					}
				} else if (var1 == 4 && var2 == 4 && var3 == 4 && var4 == 4) {
					switch (targetId) {
						case 211624:
						case 280818:
						case 213578:
						case 213579:
							qs.setQuestVar(4); // 4
							updateQuestStatus(env);
							QuestService.questTimerEnd(env);
							return true;
					}
				}
		}

		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		if (qs.getQuestVarById(0) == 3) {
			qs.setQuestVar(4); // 4
			updateQuestStatus(env);
			return true;
		}

		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		if (qs.getQuestVarById(0) == 3) {
			QuestService.questTimerEnd(env);
			qs.setQuestVar(2); // 2
			updateQuestStatus(env);

			return true;
		}

		return false;
	}
}
