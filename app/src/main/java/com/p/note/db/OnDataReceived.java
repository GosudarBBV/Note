package com.p.note.db;

import com.p.note.adapter.NoteItem;

import java.util.List;

public interface OnDataReceived {

    void onReceived(List<NoteItem> list);
}
