/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author -Nemesiss-
 */
public class SM_L2AUTH_LOGIN_CHECK extends AionServerPacket {

    /**
     * True if client is authed.
     */
    private final boolean ok;
    private final String accountName;
    private static byte[] data;

    static {
        data = hex2Byte("0000000000000101010202020303030404040505050606060707070808080909090A0A0A0B0B0B0C0C0C0D0D0D0"
                + "E0E0E0F0F0F1010101111111212121313131414141515151616161717171818181919191A1A1A1B1B1B1C1C1C1D1D1D1E1"
                + "E1E1F1F1F2020202121212222222323232424242525252626262727272828282929292A2A2A2B2B2B2C2C2C2D2D2D2E2E2"
                + "E2F2F2F3030303131313232320000000000000000000000000000000000000000000000000000000000000000000000000"
                + "00000000000000000423D3D000000000000000000000000000000000000000000000000000000000000000000000000000"
                + "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
                + "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
                + "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
                + "0000101010202020303030404040505050606060707070808080909090A0A0A0B0B0B0C0C0C0D0D0D0E0E0E0F0F0F10101"
                + "01111111212121313131414141515151616161717171818181919191A1A1A1B1B1B1C1C1C1D1D1D1E1E1E1F1F1F2020202"
                + "121212222222323232424242525252626262727272828282929292A2A2A2B2B2B2C2C2C2D2D2D2E2E2E2F2F2F303030313"
                + "131323232000000000000000000000000000000000000000000000000000000000000423D3D000000000000860010ABD71"
                + "70100804628070100F0888F060100103527070100205C2707010010161D0D020030641D0D0300203D1D0D010050B21D0D0"
                + "100408B1D0D010070001E0D010010B9FE1E010090E4512A01009011832B0100107BEA2A0100104EB9290100B050E311000"
                + "03018E2110000304513130000406C13130000B0AE7A120000C0D57A12000010CAE111000010F712130000201E13130000E"
                + "0F21413000090607A120000A0877A120000400E7C12000060FEE4110000C0CAEA110000608DE21100009002E311000080D"
                + "BE2110000E0C5E3110000C077E3110000D09EE31100005066E211000070B4E2110000E036E61100007007EA11000030FAE"
                + "6110000A00BE81100004021E7110000B032E81100000085E6110000D080E81100009055EA110000E06BF21100004056F31"
                + "1000080F2F3110000103BE41100007025E5110000902F14130000C0A41413000060BA13130000D0CB14130000509313130"
                + "000802EEA110000A05614130000800814130000B07D14130000F0191513000050D7E4110000C059E8110000A09AE511000"
                + "0B0C1E51100003089E41100009073E5110000807EC423010000BAF211000000727B12000010ACE611000090E4E71100007"
                + "096E7110000606FE711000080BDE711000060E0E911000050B9E9110000507DF3110000A07CEA11000060A4F311000000F"
                + "6E811000070CBF311000040E5F0110000B0A3EA1100002008F3110000F092F2110000403FE2110000E0237B120000F04A7"
                + "B12000030E77B120000D0FC7A12000010997B12000020C07B12000040B0E4110000F05DE611000000D8ED110000A05EEF1"
                + "10000A029E3110000E0A7E8110000705AF11100008081F111000090A8F1110000500CF11100006033F111000070E113130"
                + "000804CE51100000014E4110000F0ECE3110000306BE91100004092E91100002044E9110000F0CEE8110000101DE911000"
                + "06051EC11000010E1F2110000C0E8E5110000909E8E060100A0C58E0601002094C323020030BBC32301005009C42301006"
                + "030C42301007057C4230100907F840C0200B0CD840C0300A0A6840C0100E042850C0100C0F4840C0100D01B850C0100902"
                + "2661E010040E2C3230100106DC3230100");
    }

    /**
     * Constructs new <tt>SM_L2AUTH_LOGIN_CHECK </tt> packet
     *
     * @param ok
     */
    public SM_L2AUTH_LOGIN_CHECK(boolean ok, String accountName) {
        this.ok = ok;
        this.accountName = accountName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {
    	PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
        writeD(ok ? 0x00 : 0x01);
        writeB(data);
        writeS(accountName);
    }

    private static byte[] hex2Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
