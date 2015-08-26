package com.peebeekay.fx.info;

import java.util.HashMap;
import java.util.Map;

public enum Pair {
	
		EURGBP(9),
		EURNZD(40),
		EURAUD(14),
		EURUSD(1),
		EURCAD(15),
		EURCHF(5),
		EURJPY(10),
		
		GBPNZD(21),
		GBPAUD(22),
		GBPUSD(3),
		GBPCAD(20),
		GBPCHF(13),
		GBPJPY(11),
		
//		AUDNZD(28),
		AUDUSD(6),
		AUDCAD(16),
//		AUDCHF(39),
//		AUDJPY(17),
		
		NZDUSD(8),
		NZDCAD(91),
//		NZDCHF(89),
//		NZDJPY(19),
		
		USDCAD(7),
		USDCHF(4),
		USDJPY(2),
		
		CADCHF(90),
		CADJPY(18)
		
//		CHFJPY(12),
		
		
//		USDSEK(30),
//		EURSEK(32),
//		EURNOK(36),
//		USDNOK(37),
//		USDMXN(38),
//		USDZAR(47),
//		USDHKD(50),
//		ZARJPY(71),
//		USDTRY(83),
//		EURTRY(87),
//		EURHUF(96),
//		USDHUF(97),
//		TRYJPY(98),
//		USDCNH(105),
		;
		
		private static Map<Integer, Pair> idValueMap = new HashMap<Integer, Pair>();
		
		
	    static {
	        for (Pair pair : Pair.values()) {
	            idValueMap.put(pair.id, pair);
	        }
	    }
	
		public int id;
		Pair(int id){
			this.id = id;
		}

	    public static Pair valueOf(int id) {
	        return idValueMap.get(id);
	    }

}
