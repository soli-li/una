import { Icon, ThemeType } from '@basic/models/icon.module';

export class Utils {
  public static replaceAll(str: string, replaceValue: string, data?: string): string | undefined {
    if (data === undefined || data === null) {
      return data;
    }
    const reg = new RegExp(str, 'g');
    return data.replace(reg, replaceValue);
  }

  public static ellipsisString(length: number, data?: string): string {
    if (data === undefined) {
      return '';
    }
    let desc = data.replace(/<br\/>/g, '');
    if (desc.length > length) {
      desc = desc.substring(0, length) + '...';
    }
    return desc;
  }
  public static getValueFormMap<K, V>(map: Map<K, V>, key: K): V | null {
    const value = map.get(key) || null;
    return value;
  }

  public static splitIconName(name?: string): Icon {
    const icon = { name: '', type: '', theme: 'outline' } as Icon;
    if (name === undefined || name === null || name.trim() === '') {
      return icon;
    }
    const n = name.trim();
    const s = n.split('|');
    if (s.length !== 2) {
      return icon;
    }
    icon.name = n;
    icon.type = s[0].trim();
    let theme = 'outline' as ThemeType;
    const s1 = s[1].trim();
    if (s1 === 'fill' || s1 === 'outline' || s1 === 'twotone') {
      theme = s1;
    }

    icon.theme = theme;
    return icon;
  }
}
